package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.lobbies.api.dto.CreateLobbyDto;
import be.kdg.ipj3.lobbies.application.LobbyService;
import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class CreateLobbyTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LobbyService lobbyService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private GameService gameService;

    private JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    @Test
    void createLobby_shouldReturnLobbyId_whenValidRequest() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        given(gameService.existsById(any())).willReturn(true);

        GameId gameId = GameId.create();
        CreateLobbyDto dto = new CreateLobbyDto("pass", 2, gameId.id());

        Lobby lobby = new Lobby(
                "pass",
                2,
                gameId,
                userId
        );

        given(lobbyService.createLobby(eq("pass"), eq(2), any(GameId.class), eq(userId)))
                .willReturn(lobby);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(lobby.getId().id().toString()));

        then(userService).should().registerOrGetUser("keycloak-id", "test-user");
        then(gameService).should().existsById(gameId);
        then(lobbyService).should().createLobby("pass", 2, gameId, userId);
    }

    @Test
    void createLobby_shouldReturnNotFound_whenInvalidGameId() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        given(gameService.existsById(any())).willThrow(new NotFoundException("Game not found"));

        GameId gameId = GameId.create();
        CreateLobbyDto dto = new CreateLobbyDto("pass", 2, gameId.id());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        then(userService).should().registerOrGetUser("keycloak-id", "test-user");
        then(gameService).should().existsById(gameId);
        then(lobbyService).shouldHaveNoInteractions();
    }

    @Test
    void createLobby_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        GameId gameId = GameId.create();
        CreateLobbyDto dto = new CreateLobbyDto("pass", 2, gameId.id());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        then(userService).shouldHaveNoInteractions();
        then(gameService).shouldHaveNoInteractions();
        then(lobbyService).shouldHaveNoInteractions();
    }

    @Test
    void createLobby_shouldReturnBadRequest_whenInvalidDto() throws Exception {
        // Arrange
        String invalidJson = """
                {
                  "password": "pass",
                  "maxPlayers": 30,
                  "gameId": "%s"
                }
                """.formatted(GameId.create().id());

        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        then(gameService).shouldHaveNoInteractions();
        then(lobbyService).shouldHaveNoInteractions();
    }
}

package be.kdg.ipj3.lobbies.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.lobbies.api.dto.CreateLobbyDto;
import be.kdg.ipj3.lobbies.api.dto.GameStartDto;
import be.kdg.ipj3.lobbies.api.dto.JoinLobbyDto;
import be.kdg.ipj3.lobbies.application.LobbyService;
import be.kdg.ipj3.lobbies.domain.exception.GameServiceUnavailableException;
import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class LobbyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LobbyService lobbyService;

    @MockitoBean
    private GameService gameService;

    @MockitoBean
    private UserService userService;

    private UserId userId;
    private User mockUser;

    @BeforeEach
    void setupUser() {
        userId = UserId.create();
        Profile profile = new Profile("test-user", "");
        mockUser = new User(userId, "keycloak-id", "test-user", profile);

        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    @Test
    void getLobbies_shouldReturnListOfLobbies() throws Exception {
        // Arrange
        Lobby lobby = new Lobby(LobbyId.create(), "pass", 4, new GameId(UUID.randomUUID()), userId);
        given(lobbyService.getAllOpenLobbies()).willReturn(List.of(lobby));

        // Act & Assert
        mockMvc.perform(get("/api/lobbies")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(lobby.getId().id().toString()))
                .andExpect(jsonPath("$[0].currentPlayerCount").value(1));

        then(lobbyService).should().getAllOpenLobbies();
    }

    @Test
    void createLobby_shouldReturnLobbyId() throws Exception {
        // Arrange
        UUID gameId = UUID.randomUUID();
        CreateLobbyDto dto = new CreateLobbyDto("password", 4, gameId);
        LobbyId newLobbyId = LobbyId.create();
        Lobby createdLobby = new Lobby(newLobbyId, "password", 4, new GameId(gameId), userId);

        given(lobbyService.createLobby(anyString(), anyInt(), any(GameId.class), eq(userId)))
                .willReturn(createdLobby);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newLobbyId.id().toString()));
    }

    @Test
    void createLobby_shouldCallRegisterOrGetUser() throws Exception {
        // Arrange
        UUID gameId = UUID.randomUUID();
        CreateLobbyDto dto = new CreateLobbyDto("pwd", 4, gameId);
        LobbyId newLobbyId = LobbyId.create();
        Lobby createdLobby = new Lobby(newLobbyId, "pwd", 4, new GameId(gameId), userId);

        given(lobbyService.createLobby(anyString(), anyInt(), any(GameId.class), any(UserId.class)))
                .willReturn(createdLobby);

        // Act
        mockMvc.perform(post("/api/lobbies")
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newLobbyId.id().toString()));

        // Assert
        then(userService).should().registerOrGetUser(eq("keycloak-id"), eq("test-user"));
        then(lobbyService).should().createLobby(eq("pwd"), eq(4), eq(new GameId(gameId)), eq(userId));
    }

    @Test
    void joinLobby_shouldReturnOk() throws Exception {
        // Arrange
        UUID lobbyId = UUID.randomUUID();
        JoinLobbyDto dto = new JoinLobbyDto("password");

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/join", lobbyId)
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        then(lobbyService).should().joinLobby(eq(new LobbyId(lobbyId)), eq(userId), eq("password"));
    }

    @Test
    void startGame_shouldReturnGameUrl_whenGameServiceIsAvailable() throws Exception {
        // Arrange
        UUID gameId = UUID.randomUUID();
        UUID lobbyId = UUID.randomUUID();
        Difficulty difficulty = Difficulty.EASY;

        GameStartDto dto = new GameStartDto(gameId, List.of(), difficulty);
        String serviceUrl = "http://test.com/service";
        String gameUrl = "http://test.com/game";

        given(gameService.findServiceByGameId(new GameId(gameId))).willReturn(serviceUrl);
        given(lobbyService.startGame(serviceUrl, lobbyId.toString(), userId, difficulty))
                .willReturn(gameUrl);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId)
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.url").value(gameUrl));

        then(gameService).should().findServiceByGameId(new GameId(gameId));
        then(lobbyService).should().startGame(serviceUrl, lobbyId.toString(), userId, difficulty);
    }

    @Test
    void startGame_shouldReturnServiceUnavailable_whenGameServiceFails() throws Exception {
        // Arrange
        UUID gameId = UUID.randomUUID();
        UUID lobbyId = UUID.randomUUID();
        Difficulty difficulty = Difficulty.EASY;

        GameStartDto dto = new GameStartDto(gameId, List.of(), difficulty);
        String serviceUrl = "http://test.com/service";

        given(gameService.findServiceByGameId(new GameId(gameId))).willReturn(serviceUrl);

        given(lobbyService.startGame(serviceUrl, lobbyId.toString(), userId, difficulty))
                .willThrow(new GameServiceUnavailableException("Game service is down"));

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId)
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Game service is down"));
    }

    @Test
    void getLobbiesForGame_shouldReturnFilteredLobbies() throws Exception {
        // Arrange
        UUID gameId = UUID.randomUUID();
        Lobby lobbyForGame = new Lobby(LobbyId.create(), null, 4, new GameId(gameId), userId);

        given(lobbyService.getOpenLobbiesForGame(gameId)).willReturn(List.of(lobbyForGame));

        // Act & Assert
        mockMvc.perform(get("/api/lobbies/{gameId}", gameId)
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(lobbyForGame.getId().id().toString()))
                .andExpect(jsonPath("$[0].currentPlayerCount").value(1));

        then(lobbyService).should().getOpenLobbiesForGame(gameId);
    }

    @Test
    void getLobbiesForGame_shouldReturnEmptyList_whenNoLobbiesForGame() throws Exception {
        // Arrange
        UUID gameId = UUID.randomUUID();

        given(lobbyService.getOpenLobbiesForGame(gameId)).willReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/lobbies/{gameId}", gameId)
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        then(lobbyService).should().getOpenLobbiesForGame(gameId);
    }
}
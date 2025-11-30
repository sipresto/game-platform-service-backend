package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.lobbies.api.dto.GameStartDto;
import be.kdg.ipj3.lobbies.application.LobbyService;
import be.kdg.ipj3.lobbies.domain.exception.GameServiceUnavailableException;
import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class StartGameTests {

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

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    @Test
    void startGame_shouldReturnGameUrl_whenGameServiceIsAvailable() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        GameId gameId = GameId.create();
        LobbyId lobbyId = LobbyId.create();
        Difficulty difficulty = Difficulty.EASY;

        GameStartDto dto = new GameStartDto(gameId.id(), List.of(userId.id()), difficulty);
        String serviceUrl = "http://test.com/service";
        String gameUrl = "http://test.com/game";

        given(gameService.findServiceByGameId(any())).willReturn(serviceUrl);
        given(lobbyService.startGame(any(), any(), any(), any())).willReturn(gameUrl);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId.id())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.url").value(gameUrl));

        then(userService).should().registerOrGetUser("keycloak-id", "test-user");
        then(gameService).should().findServiceByGameId(gameId);
        then(lobbyService).should().startGame(serviceUrl, lobbyId, userId, difficulty);
    }

    @Test
    void startGame_shouldReturnServiceUnavailable_whenGameServiceFails() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        GameId gameId = GameId.create();
        LobbyId lobbyId = LobbyId.create();
        Difficulty difficulty = Difficulty.EASY;

        GameStartDto dto = new GameStartDto(gameId.id(), List.of(userId.id()), difficulty);
        String serviceUrl = "http://test.com/service";

        given(gameService.findServiceByGameId(any())).willReturn(serviceUrl);
        willThrow(new GameServiceUnavailableException("Game service is down"))
                .given(lobbyService)
                .startGame(any(), any(), any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId.id())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isServiceUnavailable());

        then(gameService).should().findServiceByGameId(gameId);
        then(lobbyService).should().startGame(serviceUrl, lobbyId, userId, difficulty);
    }

    @Test
    void startGame_shouldReturnNotFound_whenLobbyDoesNotExist() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        GameId gameId = GameId.create();
        LobbyId lobbyId = LobbyId.create();
        Difficulty difficulty = Difficulty.EASY;

        GameStartDto dto = new GameStartDto(gameId.id(), List.of(userId.id()), difficulty);
        String serviceUrl = "http://test.com/service";

        given(gameService.findServiceByGameId(any())).willReturn(serviceUrl);
        willThrow(new NotFoundException("Lobby not found"))
                .given(lobbyService)
                .startGame(any(), any(), any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId.id())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        then(gameService).should().findServiceByGameId(gameId);
        then(lobbyService).should().startGame(serviceUrl, lobbyId, userId, difficulty);
    }

    @Test
    void startGame_shouldReturnConflict_whenCallingPlayerIsNotHost() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        GameId gameId = GameId.create();
        LobbyId lobbyId = LobbyId.create();
        Difficulty difficulty = Difficulty.EASY;

        GameStartDto dto = new GameStartDto(gameId.id(), List.of(userId.id()), difficulty);
        String serviceUrl = "http://test.com/service";

        given(gameService.findServiceByGameId(any())).willReturn(serviceUrl);
        willThrow(new IllegalStateException("Only the host can start the game."))
                .given(lobbyService)
                .startGame(any(), any(), any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId.id())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());

        then(gameService).should().findServiceByGameId(gameId);
        then(lobbyService).should().startGame(serviceUrl, lobbyId, userId, difficulty);
    }

    @Test
    void startGame_shouldReturnBadRequest_whenGameIdIsNull() throws Exception {
        // Arrange
        GameStartDto dto = new GameStartDto(null, List.of(), Difficulty.EASY);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", UUID.randomUUID())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        then(userService).shouldHaveNoInteractions();
        then(gameService).shouldHaveNoInteractions();
        then(lobbyService).shouldHaveNoInteractions();
    }

    @Test
    void startGame_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        GameStartDto dto = new GameStartDto(UUID.randomUUID(), List.of(), Difficulty.EASY);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", UUID.randomUUID())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        then(gameService).shouldHaveNoInteractions();
        then(lobbyService).shouldHaveNoInteractions();
        then(userService).shouldHaveNoInteractions();
    }
}

package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.lobbies.api.dto.GameStartDto;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyId;
import be.kdg.ipj3.lobbies.infrastructure.rest.GameClient;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO: Change all tests when GameStartDto has been updated
@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class StartGameTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @MockitoBean
    private GameClient gameClient;

    @BeforeEach
    void setUp() {
        lobbyRepository.deleteAll();
        userRepository.deleteAll();
        gameRepository.deleteAll();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private UserId createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user");
        userRepository.save(user);

        return user.getId();
    }

    private GameId createTestGame() {
        Game game = new Game(
                GameId.create(),
                "Game",
                "Description",
                Money.of(BigDecimal.valueOf(19.99)),
                "http://test.com/service",
                "A rule",
                List.of(),
                List.of(Category.NATIVE),
                "",
                Instant.now()
        );
        gameRepository.save(game);

        return game.getId();
    }

    private LobbyId createTestLobby(GameId gameId, UserId userId) {
        return createTestLobby(gameId, userId, userId);
    }

    private LobbyId createTestLobby(GameId gameId, UserId userId, UserId hostId) {
        Lobby lobby = new Lobby(
                "password",
                2,
                gameId,
                hostId
        );
        lobby.join(userId, "password");
        lobbyRepository.save(lobby);

        return lobby.getId();
    }

    @Test
    void startGame_shouldReturnGameUrl_whenGameServiceIsAvailable() throws Exception {
        // Arrange
        UserId userId = createTestUser();

        GameId gameId = createTestGame();

        LobbyId lobbyId = createTestLobby(gameId, userId);

        GameCreatedResponseDto response = new GameCreatedResponseDto("http://test.com/game");
        given(gameClient.startGame(any(), any())).willReturn(response);

        GameStartDto dto = new GameStartDto(gameId.id(), Difficulty.EASY);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId.id())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.url").value("http://test.com/game"));
    }

    @Test
    void startGame_shouldReturnServiceUnavailable_whenGameServiceFails() throws Exception {
        // Arrange
        UserId userId = createTestUser();

        GameId gameId = createTestGame();

        LobbyId lobbyId = createTestLobby(gameId, userId);

        given(gameClient.startGame(any(), any())).willThrow(new RestClientException("Service unavailable"));

        GameStartDto dto = new GameStartDto(gameId.id(), Difficulty.EASY);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId.id())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void startGame_shouldReturnNotFound_whenLobbyDoesNotExist() throws Exception {
        // Arrange
        UserId userId = createTestUser();

        GameId gameId = createTestGame();

        GameStartDto dto = new GameStartDto(gameId.id(), Difficulty.EASY);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", UUID.randomUUID())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void startGame_shouldReturnConflict_whenCallingPlayerIsNotHost() throws Exception {
        // Arrange
        UserId userId = createTestUser();

        GameId gameId = createTestGame();

        LobbyId lobbyId = createTestLobby(gameId, userId, UserId.create());

        GameStartDto dto = new GameStartDto(gameId.id(), Difficulty.EASY);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId.id())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    // TODO: Change user auto-registration before enabling
    /*@Test
    void startGame_shouldReturnNotFound_whenUserNotRegistered() throws Exception {
        // Arrange
        GameId gameId = createTestGame();

        LobbyId lobbyId = createTestLobby(gameId, UserId.create());

        GameStartDto dto = new GameStartDto(gameId.id(), List.of(), Difficulty.EASY);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId.id())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }*/

    @Test
    void startGame_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        GameId gameId = createTestGame();

        LobbyId lobbyId = createTestLobby(gameId, UserId.create());

        GameStartDto dto = new GameStartDto(gameId.id(), Difficulty.EASY);

        // Act & Assert
        mockMvc.perform(post("/api/lobbies/{id}/start", lobbyId.id())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}

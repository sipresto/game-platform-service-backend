package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetLobbyTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        lobbyRepository.deleteAll();
        userRepository.deleteAll();
        gameRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private JwtRequestPostProcessor jwtForUser(String keycloakId, String username) {
        return jwt().jwt(builder -> builder
                .subject(keycloakId)
                .claim("preferred_username", username));
    }

    private void createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user", "Test@gmail.com");
        userRepository.save(user);
    }

    private GameId createTestGame() {
        Game game = new Game(
                GameId.create(),
                "Game",
                "Description",
                Money.of(new BigDecimal("19.99")),
                "/api/game",
                "/game",
                "A rule",
                List.of(),
                List.of(Category.NATIVE),
                "",
                Instant.now(),
                2
        );
        gameRepository.save(game);
        return game.getId();
    }

    @Test
    void getLobby_shouldReturnLobbyDetails_whenLobbyExistsAndUserAuthenticated() throws Exception {
        // Arrange
        createTestUser();
        GameId gameId = createTestGame();

        Optional<User> maybeUser = userRepository.findByKeycloakId("keycloak-id");
        User host = maybeUser.orElseThrow(() ->
                new AssertionError("User not found in DB")
        );

        Lobby lobby = new Lobby(
                "password",
                2,
                gameId,
                host.getId()
        );
        lobbyRepository.save(lobby);
        UUID lobbyId = lobby.getId().id();

        // Act & Assert
        mockMvc.perform(get("/api/platform/lobbies/{id}", lobbyId)
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(lobbyId.toString()))
                .andExpect(jsonPath("$.currentPlayerCount").value(1))
                .andExpect(jsonPath("$.maxPlayers").value(2))
                .andExpect(jsonPath("$.isPrivate").value(true))
                .andExpect(jsonPath("$.started").value(false))
                .andExpect(jsonPath("$.players", hasSize(1)));
    }

    @Test
    void getLobby_shouldReturnLobbyDetails_whenUserIsHost() throws Exception {
        // Arrange
        User host = User.createFromKeycloak("host-keycloak-id", "host-user", "host@gmail.com");
        userRepository.save(host);

        GameId gameId = createTestGame();

        Lobby lobby = new Lobby(
                "password",
                4,
                gameId,
                host.getId()
        );
        lobbyRepository.save(lobby);
        UUID lobbyId = lobby.getId().id();

        // Act & Assert
        mockMvc.perform(get("/api/platform/lobbies/{id}", lobbyId)
                        .with(jwtForUser("host-keycloak-id", "host-user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hostId").value(host.getId().id().toString()));
    }

    @Test
    void getLobby_shouldReturnLobbyDetails_whenUserIsParticipant() throws Exception {
        // Arrange
        User host = User.createFromKeycloak("host-keycloak-id", "host-user", "host@gmail.com");
        userRepository.save(host);

        User participant = User.createFromKeycloak("participant-keycloak-id", "participant-user", "participant@gmail.com");
        userRepository.save(participant);

        GameId gameId = createTestGame();

        Lobby lobby = new Lobby(
                "password",
                4,
                gameId,
                host.getId()
        );
        lobby.join(participant.getId(), "password");
        lobbyRepository.save(lobby);
        UUID lobbyId = lobby.getId().id();

        // Act & Assert
        mockMvc.perform(get("/api/platform/lobbies/{id}", lobbyId)
                        .with(jwtForUser("participant-keycloak-id", "participant-user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPlayerCount").value(2));
    }

    @Test
    void getLobby_shouldReturnLobbyDetails_whenLobbyHasStartedGame() throws Exception {
        // Arrange
        createTestUser();
        GameId gameId = createTestGame();

        Optional<User> maybeUser = userRepository.findByKeycloakId("keycloak-id");
        User host = maybeUser.orElseThrow(() ->
                new AssertionError("User not found in DB")
        );

        Lobby lobby = new Lobby(
                "password",
                2,
                gameId,
                host.getId()
        );
        lobby.startGame("https://game.example.com/session-123");
        lobbyRepository.save(lobby);
        UUID lobbyId = lobby.getId().id();

        // Act & Assert
        mockMvc.perform(get("/api/platform/lobbies/{id}", lobbyId)
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.started").value(true))
                .andExpect(jsonPath("$.gameUrl").value("https://game.example.com/session-123"));
    }

    @Test
    void getLobby_shouldReturnNotFound_whenLobbyDoesNotExist() throws Exception {
        // Arrange
        createTestUser();
        UUID nonExistentLobbyId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/platform/lobbies/{id}", nonExistentLobbyId)
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLobby_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        createTestUser();
        GameId gameId = createTestGame();

        Optional<User> maybeUser = userRepository.findByKeycloakId("keycloak-id");
        User host = maybeUser.orElseThrow(() ->
                new AssertionError("User not found in DB")
        );

        Lobby lobby = new Lobby(
                "password",
                2,
                gameId,
                host.getId()
        );
        lobbyRepository.save(lobby);
        UUID lobbyId = lobby.getId().id();

        // Act & Assert
        mockMvc.perform(get("/api/platform/lobbies/{id}", lobbyId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getLobby_shouldReturnForbidden_whenUserNotRegistered() throws Exception {
        // Arrange
        createTestUser();
        GameId gameId = createTestGame();

        Optional<User> maybeUser = userRepository.findByKeycloakId("keycloak-id");
        User host = maybeUser.orElseThrow(() ->
                new AssertionError("User not found in DB")
        );

        Lobby lobby = new Lobby(
                "password",
                2,
                gameId,
                host.getId()
        );
        lobbyRepository.save(lobby);
        UUID lobbyId = lobby.getId().id();

        // Act & Assert
        mockMvc.perform(get("/api/platform/lobbies/{id}", lobbyId)
                        .with(jwtForUser("non-existent-keycloak-id", "non-existent-user")))
                .andExpect(status().isNotFound());
    }
}
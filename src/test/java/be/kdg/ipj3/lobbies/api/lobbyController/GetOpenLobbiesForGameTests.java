package be.kdg.ipj3.lobbies.api.lobbyController;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetOpenLobbiesForGameTests {

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

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private void createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user");
        userRepository.save(user);
    }

    private GameId createTestGame() {
        Game game = new Game(
                GameId.create(),
                "Game",
                "Description",
                Money.of(BigDecimal.valueOf(19.99)),
                "https://test.com/service",
                "A rule",
                List.of(),
                List.of(Category.NATIVE),
                "",
                Instant.now()
        );
        gameRepository.save(game);

        return game.getId();
    }

    private void createTestLobby(GameId gameId) {
        Lobby lobby = new Lobby(
                "password",
                2,
                gameId,
                UserId.create()
        );
        lobbyRepository.save(lobby);
    }

    // TODO: Update when url gets updated
    @Test
    void getOpenLobbiesForGame_shouldReturnListOfLobbies_whenLobbiesExist() throws Exception {
        // Arrange
        createTestUser();

        GameId gameId = createTestGame();

        createTestLobby(gameId);

        // Act & Assert
        mockMvc.perform(get("/api/lobbies/game/{gameId}", gameId.id())
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].currentPlayerCount").value(1))
                .andExpect(jsonPath("$[0].isPrivate").value(true));
    }

    @Test
    void getAllOpenLobbies_shouldReturnEmptyList_whenNoLobbiesExist() throws Exception {
        // Arrange
        createTestUser();

        GameId gameId = createTestGame();

        createTestLobby(GameId.create());

        // Act & Assert
        mockMvc.perform(get("/api/lobbies/game/{gameId}", gameId.id())
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllOpenLobbies_shouldReturnNotFound_whenGameDoesNotExist() throws Exception {
        // Arrange
        createTestUser();

        createTestLobby(GameId.create());

        // Act & Assert
        mockMvc.perform(get("/api/lobbies/game/{gameId}", UUID.randomUUID())
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    // TODO: Change user auto-registration before enabling
    /*@Test
    void getAllOpenLobbies_shouldReturnNotFound_whenUserNotRegistered() throws Exception {
        // Arrange
        GameId gameId = createTestGame();

        createTestLobby(gameId);

        // Act & Assert
        mockMvc.perform(get("/api/lobbies/{gameId}", UUID.randomUUID())
                        .with(validJwt()))
                .andExpect(status().isUnauthorized());
    }*/

    @Test
    void getAllOpenLobbies_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        GameId gameId = createTestGame();

        createTestLobby(gameId);

        // Act & Assert
        mockMvc.perform(get("/api/lobbies/game/{gameId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}

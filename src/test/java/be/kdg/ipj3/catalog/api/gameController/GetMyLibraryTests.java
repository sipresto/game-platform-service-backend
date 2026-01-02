package be.kdg.ipj3.catalog.api.gameController;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.shared.GameId;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetMyLibraryTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
        userRepository.deleteAll();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
                        .subject("keycloak-id")
                        .claim("preferred_username", "test-user"));
    }

    private User createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user", "test@gmail.com");
        userRepository.save(user);
        return user;
    }

    private Game createTestGame(String name) {
        Game game = new Game(
                GameId.create(),
                name,
                "Description for " + name,
                Money.of(BigDecimal.valueOf(6.99)),
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
        return game;
    }

    @Test
    void getMyLibrary_shouldReturnListOfGames_whenUserHasGamesAndOneFavorite() throws Exception {
        // Arrange
        User user = createTestUser();
        Game game1 = createTestGame("Game1");
        Game game2 = createTestGame("Game2");
        Game game3 = createTestGame("Game3");

        user.addGame(game1.getId());
        user.addGame(game2.getId());
        user.addFavorite(game2.getId());

        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/platform/games/library")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Game1", "Game2")))
                .andExpect(jsonPath("$[?(@.name == 'Game2')].isFavorited").value(true))
                .andExpect(jsonPath("$[?(@.name == 'Game1')].isFavorited").value(false));
    }

    @Test
    void getMyLibrary_shouldReturnEmptyList_whenUserHasNoGames() throws Exception {
        // Arrange
        User user = createTestUser();

        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/platform/games/library")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getMyLibrary_shouldReturnUnauthorized_whenUserNotLoggedIn() throws Exception {
        // Arrange
        User user = createTestUser();
        Game game1 = createTestGame("Game1");
        Game game2 = createTestGame("Game2");
        Game game3 = createTestGame("Game3");

        user.addGame(game1.getId());
        user.addGame(game2.getId());
        user.addFavorite(game2.getId());

        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/platform/games/library")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}

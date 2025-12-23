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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class AddAndRemoveFavoriteGamesIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        gameRepository.deleteAll();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private User createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user", "Test@gmail.com");
        userRepository.save(user);
        return user;
    }

    private Game createTestGame() {
        Game game = new Game(
                GameId.create(),
                "Quoridor",
                "Description for " + "Quoridor",
                Money.of(new java.math.BigDecimal("19.99")),
                "/api/game",
                "/game",
                "A rule",
                List.of(),
                List.of(Category.NATIVE),
                "",
                Instant.now()
        );
        gameRepository.save(game);
        return game;
    }

    @Test
    void addFavoriteGame_shouldAddGameToListOfFavorites_whenGameIsNotFavorited() throws Exception {
        // Arrange
        User user = createTestUser();
        Game game = createTestGame();

        user.addGame(game.getId());
        userRepository.save(user);

        // Act
        mockMvc.perform(patch("/api/platform/games/favorite/{gameId}", game.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getFavoriteGames())
                .contains(game.getId());
    }

    @Test
    void addFavoriteGame_shouldReturnBadRequest_whenGameIsAlreadyFavorited() throws Exception {
        // Arrange
        User user = createTestUser();
        Game game = createTestGame();

        user.addGame(game.getId());
        userRepository.save(user);

        user.addFavorite(game.getId());
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(patch("/api/platform/games/favorite/{gameId}", game.getId().id())
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unfavoriteGame_shouldRemoveGameFromFavorites_whenGameIsFavorited() throws Exception {
        // Arrange
        User user = createTestUser();
        Game game = createTestGame();

        user.addGame(game.getId());
        userRepository.save(user);

        user.addFavorite(game.getId());
        userRepository.save(user);

        // Act
        mockMvc.perform(patch("/api/platform/games/unfavorite/{gameId}", game.getId().id())
                        .with(validJwt()))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getFavoriteGames())
                .doesNotContain(game.getId());
    }

    @Test
    void unfavoriteGame_shouldReturnBadRequest_whenGameIsNotFavorited() throws Exception {
        // Arrange
        createTestUser();
        Game game = createTestGame();

        // Act & Assert
        mockMvc.perform(patch("/api/platform/games/unfavorite/{gameId}", game.getId().id())
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFavoriteGame_shouldReturnNotFound_whenGameDoesNotExist() throws Exception {
        // Arrange
        createTestUser();
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch("/api/platform/games/favorite/{gameId}", nonExistentId)
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void addFavoriteGame_shouldReturnBadRequest_whenGameIsNotOwned() throws Exception {
        // Arrange
        createTestUser();
        Game game = createTestGame();

        // Act & Assert
        mockMvc.perform(patch("/api/platform/games/favorite/{gameId}", game.getId().id())
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

}
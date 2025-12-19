package be.kdg.ipj3.catalog.api.gameController;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetFavoriteGamesIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private Game createTestGame(String name) {
        Game game = new Game(
                GameId.create(),
                name,
                "Description for " + name,
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
    void getFavoriteGames_shouldReturnListOfGames_whenUserHasFavorites() throws Exception {
        // Arrange
        User user = createTestUser();
        Game game1 = createTestGame("Minecraft");
        Game game2 = createTestGame("Fortnite");
        Game game3 = createTestGame("Roblox");

        user.addOrRemoveFavorite(game1.getId());
        user.addOrRemoveFavorite(game3.getId());
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/games/favorites")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Minecraft")))
                .andExpect(jsonPath("$[1].name", is("Roblox")));
    }

    @Test
    void getFavoriteGames_shouldReturnEmptyListOfGames_whenUserHasNoFavorites() throws Exception {
        // Arrange
        User user = createTestUser();
        userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/games/favorites")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

}

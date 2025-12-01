package be.kdg.ipj3.shop.api.purchaseController;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.shop.domain.IPurchaseRepository;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class BuyGameTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IPurchaseRepository purchaseRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        purchaseRepository.deleteAll();
        userRepository.deleteAll();
        gameRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
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
                "Game",
                "Description",
                Money.of(BigDecimal.valueOf(19.99)),
                "https://test.com/service",
                List.of(Category.NATIVE)
        );
        gameRepository.save(game);

        return game.getId();
    }

    @Test
    void buyGame_shouldReturnOk_whenGameExists() throws Exception {
        // Arrange
        UserId userId = createTestUser();

        GameId gameId = createTestGame();

        // Act & Assert
        mockMvc.perform(post("/api/purchases/" + gameId.id())
                        .with(validJwt())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyer").value(userId.id().toString()))
                .andExpect(jsonPath("$.game").value(gameId.id().toString()));
    }

    @Test
    void buyGame_shouldReturnNotFound_whenGameDoesNotExist() throws Exception {
        // Arrange
        createTestUser();

        // Act & Assert
        mockMvc.perform(post("/api/purchases/" + UUID.randomUUID())
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    // TODO: Change user auto-registration before enabling
    /*@Test
    void buyGame_shouldReturnNotFound_whenUserNotRegistered() throws Exception {
        // Arrange
        GameId gameId = createTestGame();

        // Act & Assert
        mockMvc.perform(post("/api/purchases/" + gameId.id())
                        .with(validJwt()))
                .andExpect(status().isUnauthorized());
    }*/

    @Test
    void buyGame_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Arrange
        GameId gameId = createTestGame();

        // Act & Assert
        mockMvc.perform(post("/api/purchases/" + gameId.id()))
                .andExpect(status().isUnauthorized());
    }
}

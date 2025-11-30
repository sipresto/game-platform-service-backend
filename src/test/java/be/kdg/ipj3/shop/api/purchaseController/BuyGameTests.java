package be.kdg.ipj3.shop.api.purchaseController;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.shop.application.PurchaseService;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class BuyGameTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchaseService purchaseService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private GameService gameService;

    private JwtRequestPostProcessor validJwt() {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
                        .subject("keycloak-id")
                        .claim("preferred_username", "test-user"));
    }

    @Test
    void buyGame_shouldReturnOk_whenGameExists() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        GameId gameId = GameId.create();
        given(gameService.existsById(any())).willReturn(true);

        Purchase purchase = new Purchase(userId, gameId);
        given(purchaseService.buy(any(), any())).willReturn(purchase);

        // Act & Assert
        mockMvc.perform(post("/api/purchases/" + gameId.id())
                        .with(validJwt())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyer").value(userId.id().toString()))
                .andExpect(jsonPath("$.game").value(gameId.id().toString()));

        then(userService).should().registerOrGetUser(any(), any());
        then(gameService).should().existsById(gameId);
        then(purchaseService).should().buy(userId, gameId);
    }

    @Test
    void buyGame_shouldReturnNotFound_whenGameDoesNotExist() throws Exception {
        // Arrange
        UserId userId = UserId.create();
        User mockUser = new User(userId, "keycloak-id", "test-user", null);
        given(userService.registerOrGetUser(any(), any())).willReturn(mockUser);

        GameId gameId = GameId.create();
        given(gameService.existsById(any())).willThrow(new NotFoundException("Game not found"));

        // Act & Assert
        mockMvc.perform(post("/api/purchases/" + gameId.id())
                        .with(validJwt()))
                .andExpect(status().isNotFound());

        then(gameService).should().existsById(gameId);
        then(purchaseService).shouldHaveNoInteractions();
    }

    @Test
    void buyGame_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/purchases/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());

        then(userService).shouldHaveNoInteractions();
        then(gameService).shouldHaveNoInteractions();
        then(purchaseService).shouldHaveNoInteractions();
    }
}

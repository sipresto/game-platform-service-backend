/*package be.kdg.ipj3.shop.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.shop.application.PurchaseService;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.shop.domain.PurchaseId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
class PurchaseControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PurchaseService purchaseService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private GameService gameService;

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
                .claim("preferred_username", "test-user")
                .claim("realm_access", Map.of(
                        "roles", List.of("player")
                ))
        );
    }


    @Test
    void buyGame_ShouldReturnOk_WhenGameExists() throws Exception {
        // Arrange
        GameId gameId = GameId.create();
        PurchaseId purchaseId = PurchaseId.create();
        given(gameService.existsById(gameId)).willReturn(true);

        Purchase purchase = new Purchase(purchaseId, userId, gameId, Instant.now());
        PurchaseDto purchaseDto = new PurchaseDto(purchaseId.id(), userId.id(), gameId.id(), Instant.now());
        given(purchaseService.buy(userId, gameId)).willReturn(purchase);

        // Act + Assert
        mockMvc.perform(post("/api/purchases/" + gameId.id().toString())
                        .with(validJwt())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(purchaseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyer").value(userId.id().toString()))
                .andExpect(jsonPath("$.game").value(gameId.id().toString()));

        verify(userService).addGame(userId, gameId);
        verify(purchaseService).buy(userId, gameId);
    }

    @Test
    void buyGame_ShouldReturnBadRequest_WhenGameDoesNotExist() throws Exception {
        // Arrange
        GameId gameId = new GameId(UUID.randomUUID());

        given(gameService.existsById(gameId)).willReturn(false);

        // Act + Assert
        mockMvc.perform(post("/api/purchases/" + gameId.id().toString())
                        .with(validJwt()))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addGame(any(), any());
        verify(purchaseService, never()).buy(any(), any());
    }
}*/
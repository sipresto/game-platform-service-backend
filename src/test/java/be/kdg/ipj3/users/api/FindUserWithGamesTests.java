package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class FindUserWithGamesTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private be.kdg.ipj3.catalog.application.GameService gameService;

    @Test
    void findUserWithGames_shouldReturnUserWithGames_whenUserExists() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID gameId1 = UUID.randomUUID();
        UUID gameId2 = UUID.randomUUID();

        User user = new User(
                new UserId(userId),
                "keycloak-id",
                "testuser",
                List.of(new GameId(gameId1), new GameId(gameId2)),
                new Profile("Test User", "Test bio")
        );

        Game game1 = new Game(new GameId(gameId1), "Game 1", "Description 1", new Money(Currency.getInstance("EUR"), BigDecimal.valueOf(5)), "www.testurl.com", new ArrayList<>(), new ArrayList<>());
        Game game2 = new Game(new GameId(gameId2), "Game 2", "Description 2", new Money(Currency.getInstance("EUR"), BigDecimal.valueOf(5)), "www.testurl.com", new ArrayList<>(), new ArrayList<>());

        given(userService.getUserWithGames(new UserId(userId)))
                .willReturn(user);
        given(gameService.findGamesByIds(user.getOwnedGames()))
                .willReturn(List.of(game1, game2));

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}/games", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("authenticated-user-id")
                                        .claim("preferred_username", "authenticated-user"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games.length()").value(2))
                .andExpect(jsonPath("$.games[0].name").value("Game 1"))
                .andExpect(jsonPath("$.games[1].name").value("Game 2"));

        then(userService).should().getUserWithGames(new UserId(userId));
        then(gameService).should().findGamesByIds(user.getOwnedGames());
    }

    @Test
    void findUserWithGames_shouldReturnEmptyGamesList_whenUserHasNoGames() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        User user = new User(
                new UserId(userId),
                "keycloak-id",
                "testuser",
                List.of(),
                new Profile("Test User", "Test bio")
        );

        given(userService.getUserWithGames(new UserId(userId)))
                .willReturn(user);
        given(gameService.findGamesByIds(List.of()))
                .willReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}/games", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("authenticated-user-id")
                                        .claim("preferred_username", "authenticated-user"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games.length()").value(0));
    }

    @Test
    void findUserWithGames_shouldReturnInternalServerError_whenInvalidUserId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/{id}/games", "invalid-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("authenticated-user-id")
                                        .claim("preferred_username", "authenticated-user"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findUserWithGames_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/{id}/games", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
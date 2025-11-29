package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.application.UserService;
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
public class FindOwnedGamesByKeycloakIdTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private be.kdg.ipj3.catalog.application.GameService gameService;

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnGames_whenUserHasGames() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";
        UUID gameId1 = UUID.randomUUID();
        UUID gameId2 = UUID.randomUUID();

        List<GameId> ownedGameIds = List.of(new GameId(gameId1), new GameId(gameId2));
        Game game1 = new Game(new GameId(gameId1), "Game 1", "Description 1", new Money(Currency.getInstance("EUR"), BigDecimal.valueOf(5)), "www.testurl.com", new ArrayList<>(), new ArrayList<>());
        Game game2 = new Game(new GameId(gameId2), "Game 2", "Description 2", new Money(Currency.getInstance("EUR"), BigDecimal.valueOf(5)), "www.testurl.com", new ArrayList<>(), new ArrayList<>());

        given(userService.findOwnedGamesByKeycloakId(keycloakId))
                .willReturn(ownedGameIds);
        given(gameService.findGamesByIds(ownedGameIds))
                .willReturn(List.of(game1, game2));

        // Act & Assert
        mockMvc.perform(get("/api/user/{id}/games", keycloakId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("authenticated-user-id")
                                        .claim("preferred_username", "authenticated-user"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Game 1"))
                .andExpect(jsonPath("$[1].name").value("Game 2"));

        then(userService).should().findOwnedGamesByKeycloakId(keycloakId);
        then(gameService).should().findGamesByIds(ownedGameIds);
    }

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnNotFound_whenNoGamesFound() throws Exception {
        // Arrange
        String keycloakId = "test-keycloak-id";
        String keycloakUsername = "testuser";

        given(userService.findOwnedGamesByKeycloakId(keycloakId))
                .willReturn(List.of());
        given(gameService.findGamesByIds(List.of()))
                .willReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/user/{id}/games", keycloakId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("authenticated-user-id")
                                        .claim("preferred_username", "authenticated-user"))
                                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        then(userService).should().findOwnedGamesByKeycloakId(keycloakId);
        then(gameService).should().findGamesByIds(List.of());
    }

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/user/{id}/games", "test-keycloak-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnForbidden_whenWrongRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/user/{id}/games", "test-keycloak-id")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt
                                        .subject("authenticated-user-id")
                                        .claim("preferred_username", "authenticated-user"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
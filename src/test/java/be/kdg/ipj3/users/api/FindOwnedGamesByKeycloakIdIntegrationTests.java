package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameEntity;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameRepository;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.infrastructure.jpa.JpaProfile;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class FindOwnedGamesByKeycloakIdIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        gameRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt(String subject, String preferredUsername) {
        return jwt()
                .jwt(builder -> builder
                        .subject(subject)
                        .claim("preferred_username", preferredUsername))
                .authorities(new SimpleGrantedAuthority("ROLE_PLAYER"));
    }

    private void createUserWithOwnedGames(String keycloakId, List<UUID> gameIds) {
        JpaUserEntity user = new JpaUserEntity(
                UUID.randomUUID(),
                keycloakId,
                keycloakId + "-username",
                new JpaProfile("User", "Bio"),
                Visibility.PUBLIC
        );

        gameIds.forEach(user.getOwnedGames()::add);

        userRepository.save(user);
    }

    private void createGame(UUID id, String name) {
        JpaGameEntity game = new JpaGameEntity(
                id,
                name,
                "desc",
                BigDecimal.valueOf(5),
                "https://test.com",
                List.of()
        );

        gameRepository.save(game);
    }

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnGames_whenUserHasGames() throws Exception {
        // Arrange
        String keycloakId = "kc-user";
        UUID gameId1 = UUID.randomUUID();
        UUID gameId2 = UUID.randomUUID();

        createGame(gameId1, "Game 1");
        createGame(gameId2, "Game 2");

        createUserWithOwnedGames(keycloakId, List.of(gameId1, gameId2));

        // Act & Assert
        mockMvc.perform(get("/api/user/{id}/games", keycloakId)
                        .with(validJwt("authenticated-user", "auth-user")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Game 1"))
                .andExpect(jsonPath("$[1].name").value("Game 2"));
    }

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnNotFound_whenUserHasNoGames() throws Exception {
        // Arrange
        String keycloakId = "kc-nogames";
        createUserWithOwnedGames(keycloakId, List.of());

        // Act & Assert
        mockMvc.perform(get("/api/user/{id}/games", keycloakId)
                        .with(validJwt("authenticated-user", "auth-user")))
                .andExpect(status().isNotFound());
    }

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/user/{id}/games", "some-id"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findOwnedGamesByKeycloakId_shouldReturnForbidden_whenWrongRole() throws Exception {
        mockMvc.perform(get("/api/user/{id}/games", "some-id")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .subject("auth-user")
                                        .claim("preferred_username", "auth"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isForbidden());
    }
}

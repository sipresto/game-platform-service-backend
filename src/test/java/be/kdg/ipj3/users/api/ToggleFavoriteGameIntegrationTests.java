package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class ToggleFavoriteGameIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt().jwt(builder -> builder
                .subject("keycloak-id")
                .claim("preferred_username", "test-user"));
    }

    private User createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user", "Test@gmail.com");
        userRepository.save(user);
        return user;
    }

    @Test
    void toggleFavorite_shouldAddGame_whenNotAlreadyFavorite() throws Exception {
        // Arrange
        createTestUser();
        UUID gameId = UUID.randomUUID();

        // Act
        mockMvc.perform(put("/api/favorite/{gameId}", gameId)
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        User user = userRepository.findAllPublicUsers().get(0);
        assertThat(user.getFavoriteGames())
                .extracting(GameId::id)
                .contains(gameId);
    }

    @Test
    void toggleFavorite_shouldRemoveGame_whenAlreadyFavorite() throws Exception {
        // Arrange
        User user = createTestUser();
        UUID gameId = UUID.randomUUID();

        // Pre-seed the favorite
        user.addOrRemoveFavorite(new GameId(gameId));
        userRepository.save(user);

        // Act
        mockMvc.perform(put("/api/favorite/{gameId}", gameId)
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findAllPublicUsers().get(0);
        assertThat(updatedUser.getFavoriteGames())
                .extracting(GameId::id)
                .doesNotContain(gameId);
    }

    @Test
    void toggleFavorite_shouldReturnBadRequest_whenGameIdIsInvalid() throws Exception {
        createTestUser();

        mockMvc.perform(put("/api/favorite/{gameId}", "invalid-uuid-string")
                        .with(validJwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
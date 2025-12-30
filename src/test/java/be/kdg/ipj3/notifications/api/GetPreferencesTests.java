package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetPreferencesTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
                        .subject("keycloakId")
                        .claim("preferred_username", "username"));
    }

    private void createUser() {
        UserId userId = UserId.create();
        User user = new User(
                userId,
                "keycloakId",
                "username",
                List.of(),
                List.of(),
                0,
                new Profile("username", "Bio"),
                "Test@gmail.com"
        );
        userRepository.save(user);
    }

    @Test
    void getPreferences_shouldReturnUserPreferences() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        mockMvc.perform(get("/api/platform/notifications/preferences")
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferences.FRIEND_REQUEST.IN_APP").value(true))
                .andExpect(jsonPath("$.preferences.FRIEND_REQUEST.EMAIL").value(true))
                .andExpect(jsonPath("$.preferences.ACHIEVEMENT.IN_APP").value(true))
                .andExpect(jsonPath("$.preferences.ACHIEVEMENT.EMAIL").value(false))
                .andExpect(jsonPath("$.preferences.YOUR_TURN.IN_APP").value(true))
                .andExpect(jsonPath("$.preferences.YOUR_TURN.EMAIL").value(false))
                .andExpect(jsonPath("$.preferences.CHAT_MESSAGE.IN_APP").value(true))
                .andExpect(jsonPath("$.preferences.CHAT_MESSAGE.EMAIL").value(false));
    }

    @Test
    void getPreferences_shouldReturnForbidden_whenUserDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/platform/notifications/preferences")
                        .with(validJwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPreferences_shouldReturnUnauthorized_whenUserNotLoggedIn() throws Exception {
        // Arrange
        createUser();

        // Act & Assert - No JWT token provided
        mockMvc.perform(get("/api/platform/notifications/preferences"))
                .andExpect(status().isUnauthorized());
    }
}
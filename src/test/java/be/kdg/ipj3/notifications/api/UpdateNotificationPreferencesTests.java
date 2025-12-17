package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.notifications.domain.NotificationChannel;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.shared.NotificationType;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class UpdateNotificationPreferencesTests {

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

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
                        .subject("keycloakId")
                        .claim("preferred_username", "username"));
    }

    private UserId createUser() {
        UserId userId = UserId.create();
        User user = new User(
                userId,
                "keycloakId",
                "username",
                List.of(),
                0,
                new Profile("username", "Bio"),
                "Test@gmail.com"
        );
        userRepository.save(user);
        return userId;
    }

    @Test
    void updatePreference_shouldUpdateUserPreference() throws Exception {
        // Arrange
        UserId userId = createUser();

        // Act & Assert
        mockMvc.perform(put("/api/notifications/preferences/FRIEND_REQUEST/EMAIL")
                        .param("enabled", "false")
                        .with(validJwt()))
                .andExpect(status().isOk());

        Optional<UserNotificationPreferences> preferences = notificationRepository.findPreferencesByUserId(userId);
        assertTrue(preferences.isPresent());
        assertFalse(preferences.get().isEnabled(NotificationType.FRIEND_REQUEST, NotificationChannel.EMAIL));
    }

    @Test
    void updatePreference_shouldReturnBadRequest_whenNotificationTypeIsInvalid() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        mockMvc.perform(put("/api/notifications/preferences/INVALID_TYPE/EMAIL")
                        .param("enabled", "false")
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePreference_shouldReturnBadRequest_whenChannelIsInvalid() throws Exception {
        // Arrange
        createUser();

        // Act & Assert
        mockMvc.perform(put("/api/notifications/preferences/FRIEND_REQUEST/INVALID_CHANNEL")
                        .param("enabled", "false")
                        .with(validJwt()))
                .andExpect(status().isBadRequest());
    }
}

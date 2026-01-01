package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferencesRepository;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateNotificationPreferencesTests extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserNotificationPreferencesRepository preferencesRepository;

    @BeforeEach
    void setup() {
        notificationRepository.deleteAll();
        preferencesRepository.deleteAll();
    }

    @Test
    void updatePreference_shouldUpdateUserPreference() throws Exception {
        // Arrange
        User user = createTestUser("keycloakId", "username", "Test@gmail.com");

        // Act & Assert
        mockMvc.perform(put("/api/platform/notifications/preferences/FRIEND_REQUEST/EMAIL")
                        .param("enabled", "false")
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
                .andExpect(status().isOk());

        Optional<UserNotificationPreferences> preferences = preferencesRepository.findByUserId(user.getId());
        assertTrue(preferences.isPresent());
    }

    @Test
    void updatePreference_shouldReturnBadRequest_whenNotificationTypeIsInvalid() throws Exception {
        // Arrange
        User user = createTestUser("keycloakId", "username", "Test@gmail.com");

        // Act & Assert
        mockMvc.perform(put("/api/platform/notifications/preferences/INVALID_TYPE/EMAIL")
                        .param("enabled", "false")
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePreference_shouldReturnBadRequest_whenChannelIsInvalid() throws Exception {
        // Arrange
        User user = createTestUser("keycloakId", "username", "Test@gmail.com");

        // Act & Assert
        mockMvc.perform(put("/api/platform/notifications/preferences/FRIEND_REQUEST/INVALID_CHANNEL")
                        .param("enabled", "false")
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
                .andExpect(status().isBadRequest());
    }

    // TODO: test when user does not exist

    // TODO: test when user not logged in
}
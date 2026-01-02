package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.notifications.domain.NotificationChannel;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferencesRepository;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetPreferencesTests extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserNotificationPreferencesRepository preferencesRepository;

    @BeforeEach
    void setup() {
        notificationRepository.deleteAll();
    }

    @Test
    void getPreferences_shouldReturnUserPreferences() throws Exception {
        // Arrange
        User user = createTestUser("keycloakId", "username", "Test@gmail.com");


        // Act & Assert
        mockMvc.perform(get("/api/platform/notifications/preferences")
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
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
        // Act & Assert
        mockMvc.perform(get("/api/platform/notifications/preferences"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPreferences_shouldReturnCorrectPreferences_whenPreferencesExist() throws Exception {
        // Arrange
        User user = createTestUser("keycloakId", "username", "Test@gmail.com");

        UserNotificationPreferences preferences = preferencesRepository
                .findByUserId(user.getId())
                .orElse(new UserNotificationPreferences(user.getId()));

        preferences.updatePreference(NotificationType.FRIEND_REQUEST, NotificationChannel.IN_APP, false);
        preferencesRepository.save(preferences);

        // Act & Assert
        mockMvc.perform(get("/api/platform/notifications/preferences")
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferences.FRIEND_REQUEST.IN_APP").value(false))
                .andExpect(jsonPath("$.preferences.FRIEND_REQUEST.EMAIL").value(true))
                .andExpect(jsonPath("$.preferences.ACHIEVEMENT.IN_APP").value(true))
                .andExpect(jsonPath("$.preferences.ACHIEVEMENT.EMAIL").value(false))
                .andExpect(jsonPath("$.preferences.YOUR_TURN.IN_APP").value(true))
                .andExpect(jsonPath("$.preferences.YOUR_TURN.EMAIL").value(false))
                .andExpect(jsonPath("$.preferences.CHAT_MESSAGE.IN_APP").value(true))
                .andExpect(jsonPath("$.preferences.CHAT_MESSAGE.EMAIL").value(false));
    }
}
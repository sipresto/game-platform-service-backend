package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetPreferencesTests extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

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
                .andExpect(jsonPath("$.FRIEND_REQUEST.IN_APP").value(true))
                .andExpect(jsonPath("$.FRIEND_REQUEST.EMAIL").value(true))
                .andExpect(jsonPath("$.ACHIEVEMENT.IN_APP").value(true))
                .andExpect(jsonPath("$.ACHIEVEMENT.EMAIL").value(false))
                .andExpect(jsonPath("$.YOUR_TURN.IN_APP").value(true))
                .andExpect(jsonPath("$.YOUR_TURN.EMAIL").value(false));
    }

    // TODO: test when preferences al ingesteld zijn

    // TODO: test when user does not exist

    // TODO: test when user not logged in
}
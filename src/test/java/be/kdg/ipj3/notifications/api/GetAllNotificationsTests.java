package be.kdg.ipj3.notifications.api;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationId;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetAllNotificationsTests extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setup() {
        notificationRepository.deleteAll();
    }

    @Test
    void getAllNotifications_shouldReturnFilledNotificationDtoList_WhenThereAreNotifications() throws Exception {
        // Arrange
        User user = createTestUser("keycloakId", "username", "Test@gmail.com");

        List<NotificationId> notificationIds = List.of(
                NotificationId.create(),
                NotificationId.create()
        );

        List<Notification> notifications = List.of(
                new Notification(
                        notificationIds.get(0),
                        "Title",
                        "X-treme",
                        NotificationType.ACHIEVEMENT,
                        Instant.now(),
                        false,
                        user.getId(),
                        null
                ),
                new Notification(
                        notificationIds.get(1),
                        "Title",
                        "Someone wants to be your friend",
                        NotificationType.FRIEND_REQUEST,
                        Instant.now(),
                        true,
                        user.getId(),
                        null
                )
        );

        notificationRepository.saveAll(notifications);

        // Act && Assert
        mockMvc.perform(get("/api/platform/notifications")
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value(notificationIds.get(0).id().toString()))
                .andExpect(jsonPath("$[0].userId").value(user.getId().id().toString()))
                .andExpect(jsonPath("$[0].description").value("X-treme"))
                .andExpect(jsonPath("$[0].notificationType").value(NotificationType.ACHIEVEMENT.toString()))
                .andExpect(jsonPath("$[0].read").value(false))
                .andExpect(jsonPath("$[1].notificationId").value(notificationIds.get(1).id().toString()))
                .andExpect(jsonPath("$[1].description").value("Someone wants to be your friend"))
                .andExpect(jsonPath("$[1].notificationType").value(NotificationType.FRIEND_REQUEST.toString()))
                .andExpect(jsonPath("$[1].read").value(true));
    }

    @Test
    void getAllNotifications_shouldReturnEmptyNotificationDtoList_WhenThereAreNoNotifications() throws Exception {
        // Arrange
        User user = createTestUser("keycloakId", "username", "Test@gmail.com");

        // Act && Assert
        mockMvc.perform(get("/api/platform/notifications")
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // TODO: Test when user does not exist

    // TODO: Test when user not logged in
}
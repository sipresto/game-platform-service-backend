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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReadNotificationTests extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setup() {
        notificationRepository.deleteAll();
    }

    @Test
    void readNotification_shouldMarkNotificationAsRead() throws Exception {
        // Arrange
        User user = createTestUser("keycloakId", "username", "Test@gmail.com");

        NotificationId notificationId = NotificationId.create();
        Notification notification = new Notification(
                notificationId,
                "Test title",
                "Test notification",
                NotificationType.FRIEND_REQUEST,
                Instant.now(),
                false,
                user.getId(),
                null
        );
        notificationRepository.save(notification);

        // Act & Assert
        mockMvc.perform(patch("/api/platform/notifications/{id}/read", notificationId.id().toString())
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
                .andExpect(status().isOk());

        Optional<Notification> updatedNotification = notificationRepository.findById(notificationId);
        assertTrue(updatedNotification.isPresent() && updatedNotification.get().isRead());
    }

    @Test
    void readNotification_shouldReturnNotFound_whenNotificationDoesNotExist() throws Exception {
        // Arrange
        User user = createTestUser("keycloakId", "username", "Test@gmail.com");

        UUID nonExistentNotificationId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch("/api/platform/notifications/{id}/read", nonExistentNotificationId.toString())
                        .with(validJwt(user.getKeycloakId(), user.getUsername())))
                .andExpect(status().isNotFound());
    }

    // TODO: test when user does not exist

    // TODO: test when user not logged in
}
package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FindAllNotificationsTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UsersApi usersApi;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void findAllNotifications_shouldReturnNotificationsForUser() {
        // Arrange
        String keycloakId = "keycloakId";
        UserId userId = new UserId(UUID.randomUUID());
        User user = new User(userId, keycloakId, "username", new Profile("username", "Bio"), 0, "Test@gmail.com");
        List<Notification> notifications = List.of(
                new Notification("Notification 1", NotificationType.FRIEND_REQUEST, userId),
                new Notification("Notification 2", NotificationType.ACHIEVEMENT, userId)
        );

        given(usersApi.findByKeycloakId(keycloakId)).willReturn(user);
        given(notificationRepository.findByUserId(userId)).willReturn(notifications);

        // Act
        List<Notification> result = notificationService.findAllNotifications(keycloakId);

        // Assert
        assertThat(result).isEqualTo(notifications);
    }

    @Test
    void findAllNotifications_shouldReturnEmptyListWhenUserHasNoNotifications() {
        // Arrange
        String keycloakId = "keycloakId";
        UserId userId = new UserId(UUID.randomUUID());
        User user = new User(userId, keycloakId, "username", new Profile("username", "Bio"), 0, "Test@gmail.com");

        given(usersApi.findByKeycloakId(keycloakId)).willReturn(user);
        given(notificationRepository.findByUserId(userId)).willReturn(List.of());

        // Act
        List<Notification> result = notificationService.findAllNotifications(keycloakId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAllNotifications_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String keycloakId = "nonExistentKeycloakId";
        given(usersApi.findByKeycloakId(keycloakId)).willThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThatThrownBy(() -> notificationService.findAllNotifications(keycloakId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }
}

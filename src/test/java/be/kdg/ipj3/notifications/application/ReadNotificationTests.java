package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationId;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import be.kdg.ipj3.shared.domain.exceptions.UserNotFoundException;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReadNotificationTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UsersApi usersApi;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void readNotification_shouldMarkNotificationAsRead() {
        // Arrange
        UUID notificationId = UUID.randomUUID();
        String keycloakId = "keycloakId";
        UserId userId = new UserId(UUID.randomUUID());
        Notification notification = new Notification("Test notification", NotificationType.FRIEND_REQUEST, userId);

        given(notificationRepository.findById(new NotificationId(notificationId))).willReturn(Optional.of(notification));
        given(usersApi.findIdByKeycloakId(keycloakId)).willReturn(userId);

        // Act
        notificationService.readNotification(notificationId, keycloakId);

        // Assert
        assertThat(notification.isRead()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    void readNotification_shouldThrowExceptionWhenNotificationDoesNotExist() {
        // Arrange
        UUID notificationId = UUID.randomUUID();
        String keycloakId = "keycloakId";

        given(notificationRepository.findById(new NotificationId(notificationId))).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> notificationService.readNotification(notificationId, keycloakId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void readNotification_shouldThrowExceptionWhenNotificationDoesNotBelongToUser() {
        // Arrange
        UUID notificationId = UUID.randomUUID();
        String keycloakId = "keycloakId";
        UserId userId = new UserId(UUID.randomUUID());
        UserId anotherUserId = new UserId(UUID.randomUUID());
        Notification notification = new Notification("Test notification", NotificationType.FRIEND_REQUEST, anotherUserId);

        given(notificationRepository.findById(new NotificationId(notificationId))).willReturn(Optional.of(notification));
        given(usersApi.findIdByKeycloakId(keycloakId)).willReturn(userId);

        // Act & Assert
        assertThatThrownBy(() -> notificationService.readNotification(notificationId, keycloakId))
                .isInstanceOf(UserNotFoundException.class);
    }
}

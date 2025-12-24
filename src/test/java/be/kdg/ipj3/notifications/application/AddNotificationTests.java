package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationChannel;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddNotificationTests {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void addNotification_shouldSaveNotificationWithPreferences() {
        // Arrange
        String description = "Test notification";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        UserId userId = UserId.create();
        UserNotificationPreferences preferences = new UserNotificationPreferences(userId);
        preferences.updatePreference(type, NotificationChannel.IN_APP, true);
        preferences.updatePreference(type, NotificationChannel.EMAIL, false);

        given(notificationRepository.findPreferencesByUserId(userId)).willReturn(Optional.of(preferences));

        // Act
        notificationService.addNotification(null, description, type, Instant.now(), userId, null);

        // Assert
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        assertThat(savedNotification.getDescription()).isEqualTo(description);
        assertThat(savedNotification.getType()).isEqualTo(type);
        assertThat(savedNotification.getUser()).isEqualTo(userId);
        assertThat(savedNotification.shouldSendViaChannel(NotificationChannel.IN_APP)).isTrue();
        assertThat(savedNotification.shouldSendViaChannel(NotificationChannel.EMAIL)).isFalse();
    }

    @Test
    void addNotification_shouldUseDefaultPreferencesWhenNoneExist() {
        // Arrange
        String description = "Test notification";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        UserId userId = UserId.create();

        given(notificationRepository.findPreferencesByUserId(userId)).willReturn(Optional.empty());

        // Act
        notificationService.addNotification(null, description, type, Instant.now(), userId, null);

        // Assert
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        assertThat(savedNotification.getDescription()).isEqualTo(description);
        assertThat(savedNotification.getType()).isEqualTo(type);
        assertThat(savedNotification.getUser()).isEqualTo(userId);
        assertThat(savedNotification.shouldSendViaChannel(NotificationChannel.IN_APP)).isTrue();
        assertThat(savedNotification.shouldSendViaChannel(NotificationChannel.EMAIL)).isTrue();
    }

    @Test
    void addNotification_shouldHandleRepositorySaveError() {
        // Arrange
        String description = "Test notification";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        UserId userId = UserId.create();

        given(notificationRepository.findPreferencesByUserId(userId))
                .willReturn(Optional.empty());

        doThrow(new RuntimeException("Save failed"))
                .when(notificationRepository)
                .save(any(Notification.class));

        // Act & Assert
        assertThatThrownBy(() ->
                notificationService.addNotification(null, description, type, Instant.now(), userId, null)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Save failed");
    }

}

package be.kdg.ipj3.notifications.application;

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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdatePreferencesTests {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void updatePreferences_shouldUpdateExistingPreferences() {
        // Arrange
        UUID userId = UUID.randomUUID();
        NotificationType type = NotificationType.FRIEND_REQUEST;
        NotificationChannel channel = NotificationChannel.IN_APP;
        boolean enabled = false;

        UserNotificationPreferences existingPrefs = new UserNotificationPreferences(new UserId(userId));
        given(notificationRepository.findPreferencesByUserId(new UserId(userId))).willReturn(Optional.of(existingPrefs));

        // Act
        notificationService.updatePreferences(userId, type, channel, enabled);

        // Assert
        assertThat(existingPrefs.isEnabled(type, channel)).isEqualTo(enabled);
        verify(notificationRepository).savePreferences(existingPrefs);
    }

    @Test
    void updatePreferences_shouldCreateNewPreferencesWhenNoneExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        NotificationType type = NotificationType.FRIEND_REQUEST;
        NotificationChannel channel = NotificationChannel.IN_APP;
        boolean enabled = false;

        given(notificationRepository.findPreferencesByUserId(new UserId(userId))).willReturn(Optional.empty());

        // Act
        notificationService.updatePreferences(userId, type, channel, enabled);

        // Assert
        ArgumentCaptor<UserNotificationPreferences> prefsCaptor = ArgumentCaptor.forClass(UserNotificationPreferences.class);
        verify(notificationRepository).savePreferences(prefsCaptor.capture());

        UserNotificationPreferences savedPrefs = prefsCaptor.getValue();
        assertThat(savedPrefs.isEnabled(type, channel)).isEqualTo(enabled);
    }

    @Test
    void updatePreferences_shouldHandleRepositorySaveError() {
        // Arrange
        UUID userId = UUID.randomUUID();
        NotificationType type = NotificationType.FRIEND_REQUEST;
        NotificationChannel channel = NotificationChannel.IN_APP;
        boolean enabled = false;

        given(notificationRepository.findPreferencesByUserId(new UserId(userId))).willReturn(Optional.empty());

        doThrow(new RuntimeException("Save failed"))
                .when(notificationRepository)
                .savePreferences(any(UserNotificationPreferences.class));

        // Act & Assert
        assertThatThrownBy(() -> notificationService.updatePreferences(userId, type, channel, enabled))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Save failed");
    }
}

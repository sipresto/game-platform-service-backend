package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.domain.*;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetPreferencesTests {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void getPreferences_shouldReturnExistingPreferences() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserNotificationPreferences preferences = new UserNotificationPreferences(new UserId(userId));
        given(notificationRepository.findPreferencesByUserId(new UserId(userId))).willReturn(Optional.of(preferences));

        // Act
        UserNotificationPreferences result = notificationService.getPreferences(userId);

        // Assert
        assertThat(result).isEqualTo(preferences);
    }

    @Test
    void getPreferences_shouldReturnNewPreferencesWhenNoneExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        given(notificationRepository.findPreferencesByUserId(new UserId(userId))).willReturn(Optional.empty());

        // Act
        UserNotificationPreferences result = notificationService.getPreferences(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(new UserId(userId));
    }
}

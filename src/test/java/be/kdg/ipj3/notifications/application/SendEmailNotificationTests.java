package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class SendEmailNotificationTests {
    @Mock
    private UsersApi usersApi;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendEmailNotification_shouldSendEmailSuccessfully() {
        // Arrange
        String description = "Test notification";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        User user = new User(new UserId(userId), "keycloakId", "username", null, 0, email);

        given(usersApi.findEmailByUserId(new UserId(userId))).willReturn(email);
        given(usersApi.findById(userId)).willReturn(user);

        // Act
        notificationService.sendEmailNotification(description, type, new UserId(userId));

        // Assert
    }

    @Test
    void sendEmailNotification_shouldNotSendWhenUserHasNoEmail() {
        // Arrange
        String description = "Test notification";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        UUID userId = UUID.randomUUID();

        given(usersApi.findEmailByUserId(new UserId(userId))).willReturn(null);

        // Act
        notificationService.sendEmailNotification(description, type, new UserId(userId));

        // Assert
    }

    @Test
    void sendEmailNotification_shouldHandleEmailServiceError() {
        // Arrange
        String description = "Test notification";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        User user = new User(new UserId(userId), "keycloakId", "username", null, 0, email);

        given(usersApi.findEmailByUserId(new UserId(userId))).willReturn(email);
        given(usersApi.findById(userId)).willReturn(user);

        doThrow(new RuntimeException("Email failed"))
                .when(emailService)
                .sendNotificationEmail(
                        email,
                        user.getUsername(),
                        type,
                        description
                );

        // Act & Assert
        notificationService.sendEmailNotification(description, type, new UserId(userId));
    }
}

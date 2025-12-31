package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.domain.*;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.EmailUserDto;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AddNotificationTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserNotificationPreferencesRepository preferencesRepository;

    @Mock
    private UsersApi usersApi;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void addNotification_shouldNotAddNotification_whenUserDoesNotExist() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();
        UserId userId = new UserId(UUID.randomUUID());
        UUID referenceId = UUID.randomUUID();

        given(usersApi.existsById(userId.id())).willReturn(false);

        // Act
        notificationService.addNotification(title, description, type, sentAt, userId, referenceId);

        // Assert
        then(usersApi).should().existsById(userId.id());
        then(preferencesRepository).shouldHaveNoInteractions();
        then(notificationRepository).shouldHaveNoInteractions();
        then(emailService).shouldHaveNoInteractions();
    }

    @Test
    void addNotification_shouldSaveInAppNotification_whenInAppChannelIsEnabled() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();
        UserId userId = new UserId(UUID.randomUUID());
        UUID referenceId = UUID.randomUUID();

        UserNotificationPreferences preferences = new UserNotificationPreferences(userId);

        given(usersApi.existsById(userId.id())).willReturn(true);
        given(preferencesRepository.findByUserId(userId)).willReturn(Optional.of(preferences));
        given(notificationRepository.findByUserIdAndType(userId, type)).willReturn(List.of());

        // Act
        notificationService.addNotification(title, description, type, sentAt, userId, referenceId);

        // Assert
        then(usersApi).should().existsById(userId.id());
        then(preferencesRepository).should().findByUserId(userId);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        then(notificationRepository).should().save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        assertThat(savedNotification.getTitle()).isEqualTo(title);
        assertThat(savedNotification.getDescription()).isEqualTo(description);
        assertThat(savedNotification.getType()).isEqualTo(type);
        assertThat(savedNotification.getUser()).isEqualTo(userId);
        assertThat(savedNotification.getReferenceId()).isEqualTo(referenceId);
        assertThat(savedNotification.getCreatedAt()).isEqualTo(sentAt);
    }

    @Test
    void addNotification_shouldSendEmailNotification_whenEmailChannelIsEnabled() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();
        UserId userId = UserId.create();
        UUID referenceId = UUID.randomUUID();

        UserNotificationPreferences preferences = new UserNotificationPreferences(userId);
        preferences.updatePreference(type, NotificationChannel.IN_APP, false);
        preferences.updatePreference(type, NotificationChannel.EMAIL, true);

        given(usersApi.existsById(userId.id())).willReturn(true);
        given(preferencesRepository.findByUserId(userId)).willReturn(Optional.of(preferences));
        lenient().when(notificationRepository.findByUserIdAndType(userId, type)).thenReturn(List.of());
        lenient().when(usersApi.findEmailByUserId(userId)).thenReturn("test@gmail.com");

        // Act
        notificationService.addNotification(title, description, type, sentAt, userId, referenceId);

        // Assert
        then(usersApi).should().existsById(userId.id());
        then(preferencesRepository).should().findByUserId(userId);
        then(notificationRepository).should(never()).save(any());
    }

    @Test
    void sendEmailNotification_shouldSendEmail_whenUserHasEmail() {
        // Arrange
        UserId userId = UserId.create();
        Notification notification = new Notification(
                "Test Title",
                "Test Description",
                NotificationType.FRIEND_REQUEST,
                Instant.now(),
                userId,
                null
        );

        String email = "test@gmail.com";
        String username = "testuser";

        given(usersApi.findEmailByUserId(userId)).willReturn(email);
        given(usersApi.findEmailUserDtoByKeycloakId(userId.id())).willReturn(new EmailUserDto("kc-01", email, username));

        // Act
        notificationService.sendEmailNotification(notification);

        // Assert
        then(usersApi).should().findEmailByUserId(userId);
        then(usersApi).should().findEmailUserDtoByKeycloakId(userId.id());
        then(emailService).should().sendNotificationEmail(email, username, NotificationType.FRIEND_REQUEST, "Test Description");
    }

    @Test
    void sendEmailNotification_shouldNotSendEmail_whenUserHasNoEmail() {
        // Arrange
        UserId userId = UserId.create();
        Notification notification = new Notification(
                "Test Title",
                "Test Description",
                NotificationType.FRIEND_REQUEST,
                Instant.now(),
                userId,
                null
        );

        given(usersApi.findEmailByUserId(userId)).willReturn(null);

        // Act
        notificationService.sendEmailNotification(notification);

        // Assert
        then(usersApi).should().findEmailByUserId(userId);
        then(usersApi).should(never()).findEmailUserDtoByKeycloakId(any());
        then(emailService).shouldHaveNoInteractions();
    }

    @Test
    void sendEmailNotification_shouldNotSendEmail_whenUserEmailIsBlank() {
        // Arrange
        UserId userId = UserId.create();
        Notification notification = new Notification(
                "Test Title",
                "Test Description",
                NotificationType.FRIEND_REQUEST,
                Instant.now(),
                userId,
                null
        );

        given(usersApi.findEmailByUserId(userId)).willReturn("");

        // Act
        notificationService.sendEmailNotification(notification);

        // Assert
        then(usersApi).should().findEmailByUserId(userId);
        then(usersApi).should(never()).findEmailUserDtoByKeycloakId(any());
        then(emailService).shouldHaveNoInteractions();
    }

    @Test
    void addNotification_shouldNotRemoveNotification_whenNotDuplicate() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();
        UserId userId = new UserId(UUID.randomUUID());
        UUID referenceId = UUID.randomUUID();
        UUID differentReferenceId = UUID.randomUUID();

        Notification existingNotification = new Notification(
                new NotificationId(UUID.randomUUID()),
                "Old Title",
                "Old Description",
                type,
                sentAt.minusSeconds(60),
                false,
                userId,
                differentReferenceId
        );

        UserNotificationPreferences preferences = new UserNotificationPreferences(userId);

        given(usersApi.existsById(userId.id())).willReturn(true);
        given(preferencesRepository.findByUserId(userId)).willReturn(Optional.of(preferences));
        given(notificationRepository.findByUserIdAndType(userId, type)).willReturn(List.of(existingNotification));

        // Act
        notificationService.addNotification(title, description, type, sentAt, userId, referenceId);

        // Assert
        then(notificationRepository).should(never()).delete(any());
        then(notificationRepository).should().save(any(Notification.class));
    }

    @Test
    void addNotification_shouldCreateNewPreferences_whenPreferencesDoNotExist() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();
        UserId userId = new UserId(UUID.randomUUID());
        UUID referenceId = UUID.randomUUID();

        given(usersApi.existsById(userId.id())).willReturn(true);
        given(preferencesRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(notificationRepository.findByUserIdAndType(userId, type)).willReturn(List.of());

        // Act
        notificationService.addNotification(title, description, type, sentAt, userId, referenceId);

        // Assert
        then(preferencesRepository).should().findByUserId(userId);
        then(notificationRepository).should().save(any(Notification.class));
    }

    @Test
    void addNotification_shouldHandleBothChannelsDisabled() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.ACHIEVEMENT;
        Instant sentAt = Instant.now();
        UserId userId = new UserId(UUID.randomUUID());
        UUID referenceId = UUID.randomUUID();

        UserNotificationPreferences preferences = new UserNotificationPreferences(userId);
        preferences.updatePreference(type, NotificationChannel.IN_APP, false);
        preferences.updatePreference(type, NotificationChannel.EMAIL, false);

        given(usersApi.existsById(userId.id())).willReturn(true);
        given(preferencesRepository.findByUserId(userId)).willReturn(Optional.of(preferences));

        // Act
        notificationService.addNotification(title, description, type, sentAt, userId, referenceId);

        // Assert
        then(usersApi).should().existsById(userId.id());
        then(preferencesRepository).should().findByUserId(userId);
        then(notificationRepository).shouldHaveNoInteractions();
        then(emailService).shouldHaveNoInteractions();
    }

    @Test
    void addNotification_shouldHandleNullReferenceId() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();
        UserId userId = new UserId(UUID.randomUUID());

        UserNotificationPreferences preferences = new UserNotificationPreferences(userId);

        given(usersApi.existsById(userId.id())).willReturn(true);
        given(preferencesRepository.findByUserId(userId)).willReturn(Optional.of(preferences));
        given(notificationRepository.findByUserIdAndType(userId, type)).willReturn(List.of());

        // Act
        notificationService.addNotification(title, description, type, sentAt, userId, null);

        // Assert
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        then(notificationRepository).should().save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        assertThat(savedNotification.getReferenceId()).isNull();
    }
}
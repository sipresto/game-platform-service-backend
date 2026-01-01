package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.notifications.domain.*;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserCosmetics;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@Testcontainers
public class NotificationServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserNotificationPreferencesRepository preferencesRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User createTestUserWithEmail(String email) {
        UserId userId = UserId.create();
        User user = new User(userId, "keycloak-" + UUID.randomUUID(), "testuser", List.of(), List.of(), new UserCosmetics(), 0, new Profile("testuser", "Bio"), email);
        userRepository.save(user);
        return user;
    }

    @Test
    void addNotification_shouldNotAddNotification_whenUserDoesNotExist() {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();
        UserId nonExistentUserId = UserId.create();
        UUID referenceId = UUID.randomUUID();

        // Act
        notificationService.addNotification(title, description, type, sentAt, nonExistentUserId, referenceId);

        // Assert
        List<Notification> allNotifications = notificationRepository.findByUserId(nonExistentUserId);
        assertThat(allNotifications).isEmpty();
    }

    @Test
    void addNotification_shouldSaveInAppNotification_whenInAppChannelIsEnabled() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();
        UUID referenceId = UUID.randomUUID();

        // Act
        notificationService.addNotification(title, description, type, sentAt, user.getId(), referenceId);

        // Assert
        List<Notification> notifications = notificationRepository.findByUserId(user.getId());
        assertThat(notifications).hasSize(1);

        Notification savedNotification = notifications.getFirst();
        assertThat(savedNotification.getTitle()).isEqualTo(title);
        assertThat(savedNotification.getDescription()).isEqualTo(description);
        assertThat(savedNotification.getType()).isEqualTo(type);
        assertThat(savedNotification.getUser()).isEqualTo(user.getId());
        assertThat(savedNotification.getReferenceId()).isEqualTo(referenceId);
        assertThat(savedNotification.isRead()).isFalse();
    }

    @Test
    void addNotification_shouldCreateNewPreferences_whenPreferencesDoNotExist() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();
        UUID referenceId = UUID.randomUUID();

        Optional<UserNotificationPreferences> initialPrefs = preferencesRepository.findByUserId(user.getId());
        assertThat(initialPrefs).isEmpty();

        // Act
        notificationService.addNotification(title, description, type, sentAt, user.getId(), referenceId);

        // Assert
        List<Notification> notifications = notificationRepository.findByUserId(user.getId());
        assertThat(notifications).hasSize(1);

        Optional<UserNotificationPreferences> createdPrefs = preferencesRepository.findByUserId(user.getId());
        assertThat(createdPrefs).isPresent();
    }

    @Test
    void addNotification_shouldHandleBothChannelsDisabled() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.ACHIEVEMENT;
        Instant sentAt = Instant.now();
        UUID referenceId = UUID.randomUUID();

        UserNotificationPreferences preferences = new UserNotificationPreferences(user.getId());
        preferences.updatePreference(type, NotificationChannel.IN_APP, false);
        preferences.updatePreference(type, NotificationChannel.EMAIL, false);
        preferencesRepository.save(preferences);

        // Act
        notificationService.addNotification(title, description, type, sentAt, user.getId(), referenceId);

        // Assert
        List<Notification> notifications = notificationRepository.findByUserId(user.getId());
        assertThat(notifications).isEmpty();
    }

    @Test
    void addNotification_shouldHandleNullReferenceId() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");
        String title = "Test Title";
        String description = "Test Description";
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();

        // Act
        notificationService.addNotification(title, description, type, sentAt, user.getId(), null);

        // Assert
        List<Notification> notifications = notificationRepository.findByUserId(user.getId());
        assertThat(notifications).hasSize(1);
        assertThat(notifications.getFirst().getReferenceId()).isNull();
    }

    @Test
    void addNotification_shouldRemoveDuplicateNotification() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");
        NotificationType type = NotificationType.YOUR_TURN;
        Instant sentAt = Instant.now();

        Notification firstNotification = new Notification("Your turn", "Your turn", type, sentAt.minusSeconds(60), user.getId(), UUID.randomUUID());
        notificationRepository.save(firstNotification);

        String duplicateDescription = "Your turn";

        // Act
        notificationService.addNotification("Your turn", duplicateDescription, type, sentAt, user.getId(), UUID.randomUUID());

        // Assert
        List<Notification> notifications = notificationRepository.findByUserIdAndType(user.getId(), type);
        assertThat(notifications).hasSize(1);
    }

    @Test
    void addNotification_shouldNotRemoveNonDuplicateNotification() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");
        NotificationType type = NotificationType.FRIEND_REQUEST;
        Instant sentAt = Instant.now();

        Notification firstNotification = new Notification("Friend Request", "John sent you a friend request", type, sentAt.minusSeconds(60), user.getId(), UUID.randomUUID());
        notificationRepository.save(firstNotification);

        String differentDescription = "Jane sent you a friend request";

        // Act
        notificationService.addNotification("Friend Request", differentDescription, type, sentAt, user.getId(), UUID.randomUUID());

        // Assert
        List<Notification> notifications = notificationRepository.findByUserIdAndType(user.getId(), type);
        assertThat(notifications).hasSize(2);
    }

    @Test
    void getPreferencesByKeycloakId_shouldReturnPreferences_whenTheyExist() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");

        UserNotificationPreferences preferences = new UserNotificationPreferences(user.getId());
        preferences.updatePreference(NotificationType.FRIEND_REQUEST, NotificationChannel.EMAIL, false);
        preferencesRepository.save(preferences);

        // Act
        UserNotificationPreferences retrievedPrefs = notificationService.getPreferencesByKeycloakId(user.getKeycloakId());

        // Assert
        assertThat(retrievedPrefs.getUserId()).isEqualTo(user.getId());
        assertThat(retrievedPrefs.shouldSendViaChannel(NotificationType.FRIEND_REQUEST, NotificationChannel.EMAIL)).isFalse();
    }

    @Test
    void getPreferencesByKeycloakId_shouldCreateNewPreferences_whenTheyDontExist() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");

        // Act
        UserNotificationPreferences retrievedPrefs = notificationService.getPreferencesByKeycloakId(user.getKeycloakId());

        // Assert
        assertThat(retrievedPrefs.getUserId()).isEqualTo(user.getId());
        assertThat(retrievedPrefs.shouldSendViaChannel(NotificationType.FRIEND_REQUEST, NotificationChannel.IN_APP)).isTrue();
    }

    @Test
    void updatePreferences_shouldUpdateExistingPreferences() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");

        UserNotificationPreferences preferences = new UserNotificationPreferences(user.getId());
        preferencesRepository.save(preferences);

        // Act
        notificationService.updatePreferences(user.getKeycloakId(), NotificationType.FRIEND_REQUEST, NotificationChannel.EMAIL, false);

        // Assert
        Optional<UserNotificationPreferences> updatedPrefs = preferencesRepository.findByUserId(user.getId());
        assertThat(updatedPrefs).isPresent();
        assertThat(updatedPrefs.get().shouldSendViaChannel(NotificationType.FRIEND_REQUEST, NotificationChannel.EMAIL)).isFalse();
    }

    @Test
    void updatePreferences_shouldCreatePreferences_whenTheyDontExist() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");

        // Act
        notificationService.updatePreferences(user.getKeycloakId(), NotificationType.CHAT_MESSAGE, NotificationChannel.IN_APP, true);

        // Assert
        Optional<UserNotificationPreferences> createdPrefs = preferencesRepository.findByUserId(user.getId());
        assertThat(createdPrefs).isPresent();
        assertThat(createdPrefs.get().shouldSendViaChannel(NotificationType.CHAT_MESSAGE, NotificationChannel.IN_APP)).isTrue();
    }

    @Test
    void readNotification_shouldMarkNotificationAsRead() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");

        Notification notification = new Notification("Test Title", "Test Description", NotificationType.FRIEND_REQUEST, Instant.now(), user.getId(), UUID.randomUUID());
        notificationRepository.save(notification);

        assertThat(notification.isRead()).isFalse();

        // Act
        notificationService.readNotification(notification.getId().id(), user.getKeycloakId());

        // Assert
        Optional<Notification> updatedNotification = notificationRepository.findById(notification.getId());
        assertThat(updatedNotification).isPresent();
        assertThat(updatedNotification.get().isRead()).isTrue();
    }

    @Test
    void findAllNotifications_shouldReturnUserNotifications() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");

        Notification notification1 = new Notification("Title 1", "Description 1", NotificationType.FRIEND_REQUEST, Instant.now(), user.getId(), UUID.randomUUID());

        Notification notification2 = new Notification("Title 2", "Description 2", NotificationType.ACHIEVEMENT, Instant.now().minusSeconds(60), user.getId(), UUID.randomUUID());

        notificationRepository.saveAll(List.of(notification1, notification2));

        User otherUser = createTestUserWithEmail("other@gmail.com");
        Notification otherNotification = new Notification("Other Title", "Other Description", NotificationType.FRIEND_REQUEST, Instant.now(), otherUser.getId(), UUID.randomUUID());
        notificationRepository.save(otherNotification);

        // Act
        List<Notification> userNotifications = notificationService.findAllNotifications(user.getKeycloakId());

        // Assert
        assertThat(userNotifications).hasSize(2);
        assertThat(userNotifications).extracting(Notification::getUser).containsOnly(user.getId());
    }

    @Test
    void addNotification_shouldRespectDifferentDefaultsForNotificationTypes() {
        // Arrange
        User user = createTestUserWithEmail("test@gmail.com");
        Instant sentAt = Instant.now();

        notificationService.addNotification("Friend Request", "Test", NotificationType.FRIEND_REQUEST, sentAt, user.getId(), UUID.randomUUID());

        notificationService.addNotification("Achievement", "Test", NotificationType.ACHIEVEMENT, sentAt, user.getId(), UUID.randomUUID());

        notificationService.addNotification("Chat Message", "Test", NotificationType.CHAT_MESSAGE, sentAt, user.getId(), UUID.randomUUID());

        notificationService.addNotification("Your Turn", "Test", NotificationType.YOUR_TURN, sentAt, user.getId(), UUID.randomUUID());

        // Assert
        List<Notification> allNotifications = notificationRepository.findByUserId(user.getId());
        assertThat(allNotifications).hasSize(4);

        assertThat(allNotifications).extracting(Notification::getType).containsExactlyInAnyOrder(NotificationType.FRIEND_REQUEST, NotificationType.ACHIEVEMENT, NotificationType.CHAT_MESSAGE, NotificationType.YOUR_TURN);
    }
}
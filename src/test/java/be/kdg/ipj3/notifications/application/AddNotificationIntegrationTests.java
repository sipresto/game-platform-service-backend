package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.notifications.domain.*;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserCosmetics;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
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
public class AddNotificationIntegrationTests extends BaseIntegrationTest {

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
}
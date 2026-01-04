package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.domain.*;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.shared.domain.exceptions.UserNotFoundException;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final UserNotificationPreferencesRepository preferencesRepository;
    private final UsersApi usersApi;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository,
                               UserNotificationPreferencesRepository preferencesRepository,
                               UsersApi usersApi, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.preferencesRepository = preferencesRepository;
        this.usersApi = usersApi;
        this.emailService = emailService;
    }

    public List<Notification> findAllNotifications(String keycloakId) {
        var userId = usersApi.findIdByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));

        return notificationRepository.findByUserId(userId);
    }

    public void addNotification(String title, String description, NotificationType type, Instant sentAt, UserId userId,
                                UUID referenceId) {
        Notification notification = new Notification(title, description, type, sentAt, userId, referenceId);

        if (!usersApi.existsById(userId.id())) {
            return;
        }

        UserNotificationPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElse(preferencesRepository.save(new UserNotificationPreferences(userId)));

        if (preferences.shouldSendViaChannel(type, NotificationChannel.IN_APP)) {
            handleInAppNotification(notification);
        }

        if (preferences.shouldSendViaChannel(type, NotificationChannel.EMAIL)) {
            sendEmailNotification(notification);
        }
    }

    @Async
    public void sendEmailNotification(Notification notification) {
        UserId userId = notification.getUser();

        try {
            String userEmail = usersApi.findEmailByUserId(userId);
            if (userEmail == null || userEmail.isBlank()) {
                logger.warn("User {} has no email address, skipping email notification", userId.id());
                return;
            }

            var user = usersApi.findEmailUserDtoByKeycloakId(userId.id());
            String username = user != null ? user.username() : "User";

            emailService.sendNotificationEmail(userEmail, username, notification.getType(), notification.getDescription());

        } catch (Exception e) {
            logger.error("Failed to send email notification for user {}", userId.id(), e);
        }
    }

    public void readNotification(UUID id, String keycloakId) {
        NotificationId notificationId = new NotificationId(id);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(notificationId::notFound);
        var userId = usersApi.findIdByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));

        if (!notification.getUser().equals(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        notification.read();
        notificationRepository.save(notification);
    }

    public UserNotificationPreferences getPreferencesByKeycloakId(String keycloakId) {
        UserId userId = usersApi.findIdByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));

        return preferencesRepository.findByUserId(userId)
                .orElse(new UserNotificationPreferences(userId));
    }

    public void updatePreferences(String keycloakId, NotificationType type, NotificationChannel channel, boolean enabled) {
        UserId userId = usersApi.findIdByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));

        UserNotificationPreferences prefs = preferencesRepository.findByUserId(userId)
                .orElse(new UserNotificationPreferences(userId));

        prefs.updatePreference(type, channel, enabled);
        preferencesRepository.save(prefs);
    }

    private void handleInAppNotification(Notification notification) {
        removeDuplicateIfNeeded(notification);
        notificationRepository.save(notification);
    }

    private void removeDuplicateIfNeeded(Notification notification) {
        List<Notification> notifications = notificationRepository.findByUserIdAndType(notification.getUser(),
                notification.getType());

        notifications.stream()
                .filter(existing -> notification.getType().isDuplicate(existing, notification))
                .findFirst()
                .ifPresent(notificationRepository::delete);
    }
}

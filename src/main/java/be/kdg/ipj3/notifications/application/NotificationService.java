package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.domain.*;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.notifications.shared.NotificationsApi;
import be.kdg.ipj3.shared.domain.exceptions.UserNotFoundException;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService implements NotificationsApi {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UsersApi usersApi;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository,
                               UsersApi usersApi,
                               EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.usersApi = usersApi;
        this.emailService = emailService;
    }

    public List<Notification> findAllNotifications(String keycloakId) {
        var user = usersApi.findByKeycloakId(keycloakId);
        return notificationRepository.findByUserId(user.getId());
    }

    @Override
    public void addNotification(String description, NotificationType type, UUID id) {
        UserId userId = new UserId(id);

        Notification notification = new Notification(description, type, userId);

        notificationRepository.findPreferencesByUserId(userId)
                .ifPresent(notification::setPreferences);

        if (notification.shouldSendViaChannel(NotificationChannel.IN_APP)) {
            handleInAppNotification(description, type, id, notification);
        }

        if (notification.shouldSendViaChannel(NotificationChannel.EMAIL)) {
            sendEmailNotification(description, type, userId);
        }
    }

    private void handleInAppNotification(String description, NotificationType type, UUID id, Notification notification) {
        if (type == NotificationType.YOUR_TURN) {
            List<Notification> notifications = notificationRepository.findByUserIdAndType(id, type);
            if (!notifications.isEmpty()) {
                for (Notification notif : notifications) {
                    if (notif.getDescription().equals(description)) {
                        notificationRepository.delete(notif);
                        break;
                    }
                }
            }
        }
        notificationRepository.save(notification);
    }

    @Async
    public void sendEmailNotification(String description, NotificationType type, UserId userId) {
        try {
            String userEmail = usersApi.findEmailByUserId(userId);
            if (userEmail == null || userEmail.isBlank()) {
                logger.warn("User {} has no email address, skipping email notification", userId.id());
                return;
            }

            var user = usersApi.findById(userId.id());
            String username = user != null ? user.getUsername() : "User";

            emailService.sendNotificationEmail(userEmail, username, type, description);

        } catch (Exception e) {
            logger.error("Failed to send email notification for user {}", userId.id(), e);
        }
    }

    public void readNotification(UUID id, String keycloakid) {
        NotificationId notificationId = new NotificationId(id);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(notificationId::notFound);
        var userId = usersApi.findIdByKeycloakId(keycloakid);
        if(!notification.getUser().equals(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        notification.read();
        notificationRepository.save(notification);
    }

    public void updatePreferences(UUID userId, NotificationType type,
                                  NotificationChannel channel, boolean enabled) {
        UserNotificationPreferences prefs = notificationRepository
                .findPreferencesByUserId(new UserId(userId))
                .orElse(new UserNotificationPreferences(new UserId(userId)));

        prefs.updatePreference(type, channel, enabled);
        notificationRepository.savePreferences(prefs);
    }

    public UserNotificationPreferences getPreferences(UUID userId) {
        return notificationRepository
                .findPreferencesByUserId(new UserId(userId))
                .orElse(new UserNotificationPreferences(new UserId(userId)));
    }
}
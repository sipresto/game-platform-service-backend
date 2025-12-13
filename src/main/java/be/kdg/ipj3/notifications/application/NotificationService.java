package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.domain.*;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.notifications.shared.NotificationsApi;
import be.kdg.ipj3.shared.domain.exceptions.UserNotFoundException;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService implements NotificationsApi {
    private final NotificationRepository notificationRepository;
    private final UsersApi usersApi;

    public NotificationService(NotificationRepository notificationRepository, UsersApi usersApi) {
        this.notificationRepository = notificationRepository;
        this.usersApi = usersApi;
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

        // TODO: Add email sending logic here
        // if (notification.shouldSendViaChannel(NotificationChannel.EMAIL)) {
        //     sendEmailNotification(description, type, userId);
        // }
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
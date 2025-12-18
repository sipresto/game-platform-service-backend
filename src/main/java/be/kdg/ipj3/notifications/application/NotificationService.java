package be.kdg.ipj3.notifications.application;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationId;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.notifications.shared.NotificationsApi;
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
    public void addNotification(String description, NotificationType type, UUID userId) {
        Notification notification = null;
        if (type == NotificationType.YOUR_TURN) {
            List<Notification> notifications = notificationRepository.findByUserIdAndType(userId, type);
            if (!notifications.isEmpty()) {
                for (Notification notif : notifications) {
                    if (notif.getDescription().equals(description)) {
                        notification = notif;
                    }
                }
                if (notification != null) {
                    notificationRepository.delete(notification);
                }
            }
        }
        notification = new Notification(description, type, new UserId(userId));
        notificationRepository.save(notification);
    }

    public void readNotification(UUID id) {
        NotificationId notificationId = new NotificationId(id);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(notificationId::notFound);

        notification.read();
        notificationRepository.save(notification);
    }
}

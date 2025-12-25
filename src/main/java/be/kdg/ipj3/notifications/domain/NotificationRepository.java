package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    List<Notification> findByUserId(UserId id);
    Optional<Notification> findById(NotificationId id);
    void save(Notification notification);
    void delete(Notification notification);
    void deleteAll();
    void saveAll(List<Notification> notifications);
    List<Notification> findByUserIdAndType(UserId id, NotificationType type);
    Optional<UserNotificationPreferences> findPreferencesByUserId(UserId userId);
    void savePreferences(UserNotificationPreferences preferences);
}
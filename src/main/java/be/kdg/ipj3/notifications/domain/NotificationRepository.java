package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository {
    List<Notification> findByUserId(UserId id);

    void deleteAll();

    void saveAll(List<Notification> notifications);

    void save(Notification notification);

    List<Notification> findByUserIdAndType(UUID id, NotificationType type);

    void delete(Notification notification);
}

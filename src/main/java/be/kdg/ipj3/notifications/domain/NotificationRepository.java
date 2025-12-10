package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository {
    List<Notification> findByUserId(UserId id);

    void deleteAll();

    void saveAll(List<Notification> notifications);
}

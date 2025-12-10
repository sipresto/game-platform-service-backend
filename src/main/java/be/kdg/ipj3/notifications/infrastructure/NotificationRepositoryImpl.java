package be.kdg.ipj3.notifications.infrastructure;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.infrastructure.jpa.JpaNotificationEntity;
import be.kdg.ipj3.notifications.infrastructure.jpa.JpaNotificationRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    private final JpaNotificationRepository jpaNotificationRepository;

    public NotificationRepositoryImpl(JpaNotificationRepository jpaNotificationRepository) {
        this.jpaNotificationRepository = jpaNotificationRepository;
    }

    @Override
    public List<Notification> findByUserId(UserId id) {
        return jpaNotificationRepository.findByUserId(id.id())
                .stream().map(JpaNotificationEntity::toDomain).toList();
    }

    @Override
    public void deleteAll() {
        jpaNotificationRepository.deleteAll();
    }

    @Override
    public void saveAll(List<Notification> notifications) {
        notifications.stream().map(JpaNotificationEntity::fromDomain).forEach(jpaNotificationRepository::save);
    }

    @Override
    public void save(Notification notification) {
        jpaNotificationRepository.save(JpaNotificationEntity.fromDomain(notification));
    }
}

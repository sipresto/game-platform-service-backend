package be.kdg.ipj3.notifications.infrastructure;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationId;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.infrastructure.jpa.JpaNotificationEntity;
import be.kdg.ipj3.notifications.infrastructure.jpa.JpaNotificationRepository;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Optional<Notification> findById(NotificationId id) {
        return jpaNotificationRepository.findById(id.id())
                .map(JpaNotificationEntity::toDomain);
    }

    @Override
    public void save(Notification notification) {
        jpaNotificationRepository.save(JpaNotificationEntity.fromDomain(notification));
    }

    @Override
    public List<Notification> findByUserIdAndType(UserId userId, NotificationType type) {
        return jpaNotificationRepository.findByUserIdAndType(userId.id(), type)
                .stream().map(JpaNotificationEntity::toDomain).toList();
    }

    @Override
    public void delete(Notification notification) {
        jpaNotificationRepository.delete(JpaNotificationEntity.fromDomain(notification));
    }

    @Override
    public void deleteAll() {
        jpaNotificationRepository.deleteAll();
    }

    @Override
    public void saveAll(List<Notification> notifications) {
        notifications.stream()
                .map(JpaNotificationEntity::fromDomain)
                .forEach(jpaNotificationRepository::save);
    }
}
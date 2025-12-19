package be.kdg.ipj3.notifications.infrastructure;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationId;
import be.kdg.ipj3.notifications.domain.NotificationRepository;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.infrastructure.jpa.JpaNotificationEntity;
import be.kdg.ipj3.notifications.infrastructure.jpa.JpaNotificationRepository;
import be.kdg.ipj3.notifications.infrastructure.jpa.JpaUserNotificationPreferencesEntity;
import be.kdg.ipj3.notifications.infrastructure.jpa.JpaUserNotificationPreferencesRepository;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    private final JpaNotificationRepository jpaNotificationRepository;
    private final JpaUserNotificationPreferencesRepository jpaPreferencesRepository;

    public NotificationRepositoryImpl(JpaNotificationRepository jpaNotificationRepository,
                                      JpaUserNotificationPreferencesRepository jpaPreferencesRepository) {
        this.jpaNotificationRepository = jpaNotificationRepository;
        this.jpaPreferencesRepository = jpaPreferencesRepository;
    }

    @Override
    public List<Notification> findByUserId(UserId id) {
        List<Notification> notifications = jpaNotificationRepository.findByUserId(id.id())
                .stream().map(JpaNotificationEntity::toDomain).toList();

        loadPreferencesForNotifications(notifications);

        return notifications;
    }

    @Override
    public Optional<Notification> findById(NotificationId id) {
        Optional<Notification> notification = jpaNotificationRepository.findById(id.id())
                .map(JpaNotificationEntity::toDomain);

        notification.ifPresent(notif ->
                findPreferencesByUserId(notif.getUser()).ifPresent(notif::setPreferences)
        );

        return notification;
    }

    @Override
    public void save(Notification notification) {
        jpaNotificationRepository.save(JpaNotificationEntity.fromDomain(notification));

        notification.getPreferences().ifPresent(this::savePreferences);
    }

    @Override
    public Optional<UserNotificationPreferences> findPreferencesByUserId(UserId userId) {
        return jpaPreferencesRepository.findById(userId.id())
                .map(JpaUserNotificationPreferencesEntity::toDomain);
    }

    @Override
    public void savePreferences(UserNotificationPreferences preferences) {
        jpaPreferencesRepository.save(JpaUserNotificationPreferencesEntity.fromDomain(preferences));
    }

    private void loadPreferencesForNotifications(List<Notification> notifications) {
        if (notifications.isEmpty()) return;

        UserId userId = notifications.getFirst().getUser();

        Optional<UserNotificationPreferences> prefs = findPreferencesByUserId(userId);

        notifications.forEach(notification ->
                notification.setPreferences(prefs.orElse(null))
        );
    }

    @Override
    public List<Notification> findByUserIdAndType(UUID id, NotificationType type) {
        return jpaNotificationRepository.findByUserIdAndType(id, type)
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
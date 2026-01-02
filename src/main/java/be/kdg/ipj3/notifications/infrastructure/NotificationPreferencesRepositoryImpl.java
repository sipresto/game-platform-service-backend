package be.kdg.ipj3.notifications.infrastructure;

import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferencesRepository;
import be.kdg.ipj3.notifications.infrastructure.jpa.JpaUserNotificationPreferencesEntity;
import be.kdg.ipj3.notifications.infrastructure.jpa.JpaUserNotificationPreferencesRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class NotificationPreferencesRepositoryImpl implements UserNotificationPreferencesRepository {
    private final JpaUserNotificationPreferencesRepository jpaPreferencesRepository;

    public NotificationPreferencesRepositoryImpl(JpaUserNotificationPreferencesRepository jpaPreferencesRepository) {
        this.jpaPreferencesRepository = jpaPreferencesRepository;
    }

    @Override
    public Optional<UserNotificationPreferences> findByUserId(UserId userId) {
        return jpaPreferencesRepository.findByUserId(userId.id())
                .map(JpaUserNotificationPreferencesEntity::toDomain);
    }

    @Override
    public UserNotificationPreferences save(UserNotificationPreferences preferences) {
        jpaPreferencesRepository.save(JpaUserNotificationPreferencesEntity.fromDomain(preferences));
        return preferences;
    }

    @Override
    public void deleteAll() {
        jpaPreferencesRepository.deleteAll();
    }
}

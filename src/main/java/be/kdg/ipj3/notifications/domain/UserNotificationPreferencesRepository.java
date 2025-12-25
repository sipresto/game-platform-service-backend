package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.users.shared.UserId;

import java.util.Optional;

public interface UserNotificationPreferencesRepository {
    Optional<UserNotificationPreferences> findByUserId(UserId userId);

    void save(UserNotificationPreferences preferences);

    void deleteAll();
}

package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.EnumMap;
import java.util.Map;

@AggregateRoot
public class UserNotificationPreferences {
    @Identity
    private final UserId userId;
    private final Map<NotificationType, Map<NotificationChannel, Boolean>> preferences;

    public UserNotificationPreferences(UserId userId) {
        this(userId, new EnumMap<>(NotificationType.class));
        initializeDefaultPreferences();
    }

    public UserNotificationPreferences(UserId userId,
                                       Map<NotificationType, Map<NotificationChannel, Boolean>> preferences) {
        this.userId = userId;
        this.preferences = new EnumMap<>(preferences);
    }

    public UserId getUserId() {
        return userId;
    }

    public Map<NotificationType, Map<NotificationChannel, Boolean>> getPreferences() {
        return Map.copyOf(preferences);
    }

    public void updatePreference(NotificationType type, NotificationChannel channel, boolean enabled) {
        if (type == null || channel == null) {
            return;
        }

        preferences.get(type).put(channel, enabled);
    }

    public boolean shouldSendViaChannel(NotificationType type, NotificationChannel channel) {
        return preferences.get(type).get(channel);
    }

    private void initializeDefaultPreferences() {
        for (NotificationType type : NotificationType.values()) {
            Map<NotificationChannel, Boolean> channelSettings = new EnumMap<>(NotificationChannel.class);

            for (NotificationChannel channel : NotificationChannel.values()) {
                channelSettings.put(channel, type.defaultFor(channel));
            }

            preferences.put(type, channelSettings);
        }
    }
}
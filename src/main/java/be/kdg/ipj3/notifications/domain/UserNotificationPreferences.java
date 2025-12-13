package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.EnumMap;
import java.util.Map;

@Entity
public class UserNotificationPreferences {
    @Identity
    private final UserId userId;

    private final Map<NotificationType, Map<NotificationChannel, Boolean>> preferences;

    public UserNotificationPreferences(UserId userId) {
        this.userId = userId;
        this.preferences = new EnumMap<>(NotificationType.class);
        initializeDefaultPreferences();
    }

    public UserNotificationPreferences(UserId userId,
                                       Map<NotificationType, Map<NotificationChannel, Boolean>> preferences) {
        this.userId = userId;
        this.preferences = new EnumMap<>(preferences);
    }

    private void initializeDefaultPreferences() {
        for (NotificationType type : NotificationType.values()) {
            Map<NotificationChannel, Boolean> channelSettings = new EnumMap<>(NotificationChannel.class);
            for (NotificationChannel channel : NotificationChannel.values()) {
                boolean defaultValue = (type != NotificationType.YOUR_TURN);
                channelSettings.put(channel, defaultValue);
            }
            preferences.put(type, channelSettings);
        }
    }

    public void updatePreference(NotificationType type, NotificationChannel channel, boolean enabled) {
        validateTypeAndChannel(type, channel);
        preferences.get(type).put(channel, enabled);
    }

    public void updateAllChannelsForType(NotificationType type, boolean enabled) {
        validateType(type);
        for (NotificationChannel channel : NotificationChannel.values()) {
            preferences.get(type).put(channel, enabled);
        }
    }

    public boolean isEnabled(NotificationType type, NotificationChannel channel) {
        validateTypeAndChannel(type, channel);
        return preferences.get(type).get(channel);
    }

    public Map<NotificationChannel, Boolean> getPreferencesForType(NotificationType type) {
        validateType(type);
        return new EnumMap<>(preferences.get(type));
    }

    public Map<NotificationType, Map<NotificationChannel, Boolean>> getAllPreferences() {
        Map<NotificationType, Map<NotificationChannel, Boolean>> copy = new EnumMap<>(NotificationType.class);
        preferences.forEach((type, channelMap) ->
                copy.put(type, new EnumMap<>(channelMap)));
        return copy;
    }

    private void validateType(NotificationType type) {
        if (type == null) throw new IllegalArgumentException("Notification type cannot be null");
    }

    private void validateTypeAndChannel(NotificationType type, NotificationChannel channel) {
        validateType(type);
        if (channel == null) throw new IllegalArgumentException("Notification channel cannot be null");
    }

    public UserId getUserId() { return userId; }
}
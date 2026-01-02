package be.kdg.ipj3.notifications.api.dto;

import be.kdg.ipj3.notifications.domain.NotificationChannel;
import be.kdg.ipj3.notifications.domain.UserNotificationPreferences;
import be.kdg.ipj3.notifications.shared.NotificationType;

import java.util.Map;

public record NotificationPreferencesDto(
        Map<NotificationType, Map<NotificationChannel, Boolean>> preferences
) {
    public static NotificationPreferencesDto fromDomain(UserNotificationPreferences prefs) {
        return new NotificationPreferencesDto(prefs.getPreferences());
    }
}
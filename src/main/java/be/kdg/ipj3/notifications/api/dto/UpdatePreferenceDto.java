package be.kdg.ipj3.notifications.api.dto;

import be.kdg.ipj3.notifications.domain.NotificationChannel;
import be.kdg.ipj3.notifications.shared.NotificationType;
import jakarta.validation.constraints.NotNull;

public record UpdatePreferenceDto(
        @NotNull
        NotificationType notificationType,

        @NotNull
        NotificationChannel channel,

        @NotNull
        Boolean enabled
) {
}
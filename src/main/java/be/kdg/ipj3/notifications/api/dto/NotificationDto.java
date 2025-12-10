package be.kdg.ipj3.notifications.api.dto;

import be.kdg.ipj3.notifications.domain.Notification;
import be.kdg.ipj3.notifications.domain.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
        UUID notificationId,
        UUID userId,
        String description,
        NotificationType notificationType,
        Instant createdAt,
        boolean read
) {
    public static NotificationDto fromDomain(Notification notification) {
        return new NotificationDto(
                notification.getId().id(),
                notification.getUser().id(),
                notification.getDescription(),
                notification.getType(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }
}

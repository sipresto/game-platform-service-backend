package be.kdg.ipj3.notifications.infrastructure.rabbitmq.dto;

import be.kdg.ipj3.notifications.shared.NotificationType;

import java.util.UUID;

public record TurnNotificationDto(
        UUID userId,
        String description,
        NotificationType notificationType
) {
}

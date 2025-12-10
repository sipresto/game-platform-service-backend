package be.kdg.ipj3.notifications.infrastructure.rabbitmq.dto;

import java.util.UUID;

public record TurnNotificationDto(
        UUID userId,
        String description
) {
}

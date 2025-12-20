package be.kdg.ipj3.notifications.infrastructure.rabbitmq.dto;

public record TurnNotificationDto(
        String userId,
        String description
) {
}

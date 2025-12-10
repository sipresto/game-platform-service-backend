package be.kdg.ipj3.notifications.infrastructure.rabbitmq;

import be.kdg.ipj3.notifications.application.NotificationService;
import be.kdg.ipj3.notifications.infrastructure.rabbitmq.config.RabbitMQTopologyNotifications;
import be.kdg.ipj3.notifications.infrastructure.rabbitmq.dto.TurnNotificationDto;
import be.kdg.ipj3.notifications.shared.NotificationType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageHandler {

    private final NotificationService notificationService;

    public NotificationMessageHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQTopologyNotifications.TURN_NOTIFICATIONS_QUEUE)
    void onNotificationMessageReceived(TurnNotificationDto dto) {
        notificationService.addNotification(dto.description(), NotificationType.YOUR_TURN, dto.userId());
    }
}

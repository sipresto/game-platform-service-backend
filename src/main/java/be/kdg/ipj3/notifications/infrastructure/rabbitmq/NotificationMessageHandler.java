package be.kdg.ipj3.notifications.infrastructure.rabbitmq;

import be.kdg.ipj3.notifications.application.NotificationService;
import be.kdg.ipj3.notifications.infrastructure.rabbitmq.config.RabbitMQTopologyNotifications;
import be.kdg.ipj3.notifications.infrastructure.rabbitmq.dto.TurnNotificationDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageHandler {

    private final NotificationService notificationService;

    public NotificationMessageHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQTopologyNotifications.TURN_NOTIFICATION_QUEUE)
    void onNotificationMessageReceived(TurnNotificationDto dto) {
        notificationService.addNotification(dto.description(), dto.notificationType(), dto.userId());
    }
}

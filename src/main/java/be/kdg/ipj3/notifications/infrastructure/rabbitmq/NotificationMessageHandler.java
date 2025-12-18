package be.kdg.ipj3.notifications.infrastructure.rabbitmq;

import be.kdg.ipj3.notifications.application.NotificationService;
import be.kdg.ipj3.notifications.infrastructure.rabbitmq.config.RabbitMQTopologyNotifications;
import be.kdg.ipj3.notifications.infrastructure.rabbitmq.dto.TurnNotificationDto;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageHandler {

    private final NotificationService notificationService;
    private final UsersApi usersApi;

    public NotificationMessageHandler(NotificationService notificationService, UsersApi usersApi) {
        this.notificationService = notificationService;
        this.usersApi = usersApi;
    }

    @RabbitListener(queues = RabbitMQTopologyNotifications.TURN_NOTIFICATIONS_QUEUE)
    void onNotificationMessageReceived(TurnNotificationDto dto) {
        UserId userId = usersApi.findIdByKeycloakId(dto.userId());
        notificationService.addNotification(dto.description(), NotificationType.YOUR_TURN, userId.id());
    }
}

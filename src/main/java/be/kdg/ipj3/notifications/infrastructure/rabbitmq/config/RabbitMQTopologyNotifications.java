package be.kdg.ipj3.notifications.infrastructure.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopologyNotifications {

    public static final String TURN_NOTIFICATION_QUEUE = "turn-notification-queue";

    @Bean
    Queue turnNotificationsQueue() {
        return QueueBuilder.durable(TURN_NOTIFICATION_QUEUE).build();
    }

    @Bean
    Binding notificationBinding(Queue turnNotificationsQueue, TopicExchange notificationsExchange) {
        return BindingBuilder.bind(turnNotificationsQueue)
                .to(notificationsExchange)
                .with("turn.notification.*");
    }

}

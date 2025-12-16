package be.kdg.ipj3.catalog.infrastructure.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopologyRegistrations {

    public static final String GAME_REGISTRATION_QUEUE = "game-registration-queue";

    @Bean
    Queue gameRegistrationQueue() {
        return QueueBuilder.durable(GAME_REGISTRATION_QUEUE).build();
    }

    @Bean
    Binding registrationsBinding(Queue gameRegistrationQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(gameRegistrationQueue)
                .to(gameEventsExchange)
                .with("game.register.*");
    }
}

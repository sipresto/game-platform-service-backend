package be.kdg.ipj3.sessions.infrastructure.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopologySessions {

    public static final String SESSION_GAME_RESULTS_QUEUE = "sessions-game-results-queue";

    @Bean
    Queue sessionsGameResultsQueue() {
        return QueueBuilder.durable(SESSION_GAME_RESULTS_QUEUE).build();
    }

    @Bean
    Binding sessionsBinding(Queue sessionsGameResultsQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(sessionsGameResultsQueue)
                .to(gameEventsExchange)
                .with("game.result.*");
    }
}



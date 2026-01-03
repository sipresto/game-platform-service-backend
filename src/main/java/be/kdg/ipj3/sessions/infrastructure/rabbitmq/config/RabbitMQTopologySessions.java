package be.kdg.ipj3.sessions.infrastructure.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopologySessions {

    public static final String SESSION_GAME_RESULTS_QUEUE = "sessions-game-results-queue";
    public static final String SESSION_GAME_REMATCH_QUEUE = "session-game-rematch-queue";
    public static final String SESSION_GAME_START_QUEUE = "session-game-start-queue";

    @Bean
    Queue sessionsGameResultsQueue() {
        return QueueBuilder.durable(SESSION_GAME_RESULTS_QUEUE).build();
    }

    @Bean
    Binding sessionsResultBinding(Queue sessionsGameResultsQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(sessionsGameResultsQueue)
                .to(gameEventsExchange)
                .with("game.result.*");
    }

    @Bean
    Queue sessionsGameRematchQueue() {
        return QueueBuilder.durable(SESSION_GAME_REMATCH_QUEUE).build();
    }

    @Bean
    Binding sessionsRematchBinding(Queue sessionsGameRematchQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(sessionsGameRematchQueue)
                .to(gameEventsExchange)
                .with("game.rematch.*");
    }

    @Bean
    Queue sessionsGameStartQueue() {
        return QueueBuilder.durable(SESSION_GAME_START_QUEUE).build();
    }

    @Bean
    Binding sessionsStartBinding(Queue sessionsGameStartQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(sessionsGameStartQueue)
                .to(gameEventsExchange)
                .with("game.start.*");
    }
}



package be.kdg.ipj3.shared.infrastructure.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    public static final String GAME_RESULTS_QUEUE = "platform-game-results-queue";

    public static final String GAME_EVENTS_EXCHANGE = "game-events-exchange";

    @Bean
    TopicExchange gameEventsExchange() {
        return new TopicExchange(GAME_EVENTS_EXCHANGE);
    }

    @Bean
    Queue gameResultsQueue() {
        return QueueBuilder.durable(GAME_RESULTS_QUEUE).build();
    }

    @Bean
    Binding binding(Queue gameResultsQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(gameResultsQueue)
                .to(gameEventsExchange)
                .with("game.result.*");
    }
}



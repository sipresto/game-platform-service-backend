package be.kdg.ipj3.shared.infrastructure.rabbitmq.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopologyShared {
    public static final String GAME_EVENTS_EXCHANGE = "game-events-exchange";

    @Bean
    public TopicExchange gameEventsExchange() {
        return new TopicExchange(GAME_EVENTS_EXCHANGE);
    }
}
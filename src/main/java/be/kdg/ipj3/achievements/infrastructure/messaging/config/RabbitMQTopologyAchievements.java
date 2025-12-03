package be.kdg.ipj3.achievements.infrastructure.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopologyAchievements {

    public static final String ACHIEVEMENT_GAME_RESULTS_QUEUE = "achievements-game-results-queue";

    @Bean
    Queue achievementsGameResultsQueue() {
        return QueueBuilder.durable(ACHIEVEMENT_GAME_RESULTS_QUEUE).build();
    }

    @Bean
    Binding achievementsBinding(Queue achievementsGameResultsQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(achievementsGameResultsQueue)
                .to(gameEventsExchange)
                .with("game.result.*");
    }
}



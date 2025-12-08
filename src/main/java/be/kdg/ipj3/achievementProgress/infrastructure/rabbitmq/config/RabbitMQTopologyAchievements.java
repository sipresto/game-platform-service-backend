package be.kdg.ipj3.achievementProgress.infrastructure.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopologyAchievements {

    public static final String ACHIEVEMENT_GAME_RESULTS_QUEUE = "achievements-game-results-queue";
    public static final String ACHIEVEMENT_COMPLETED_QUEUE = "achievement-completed-queue";

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

    @Bean
    Queue achievementCompletedQueue() {
        return QueueBuilder.durable(ACHIEVEMENT_COMPLETED_QUEUE).build();
    }

    @Bean
    Binding achievementCompletedBinding(Queue achievementCompletedQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(achievementCompletedQueue)
                .to(gameEventsExchange)
                .with("achievement.completed.*");
    }
}

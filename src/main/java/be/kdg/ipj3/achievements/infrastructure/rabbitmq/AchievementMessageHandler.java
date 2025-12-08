package be.kdg.ipj3.achievements.infrastructure.rabbitmq;

import be.kdg.ipj3.achievements.application.AchievementProgressService;
import be.kdg.ipj3.achievements.domain.GameContext;
import be.kdg.ipj3.achievements.infrastructure.rabbitmq.config.RabbitMQTopologyAchievements;
import be.kdg.ipj3.achievements.infrastructure.rabbitmq.dto.AchievementCompletedDto;
import be.kdg.ipj3.shared.infrastructure.rabbitmq.dto.GameResultMessageDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AchievementMessageHandler {

    private final AchievementProgressService progressService;

    public AchievementMessageHandler(AchievementProgressService progressService) {
        this.progressService = progressService;
    }

    @RabbitListener(queues = RabbitMQTopologyAchievements.ACHIEVEMENT_GAME_RESULTS_QUEUE)
    void onGameResultMessageReceived(GameResultMessageDto dto) {
        for (UUID player : dto.players()) {
            progressService.evaluateAchievementsAfterGame(new GameContext(
                    dto.gameId(),
                    player,
                    dto.winnerId() != null
                            ? dto.winnerId()
                            : null,
                    dto.sessionResult(),
                    dto.difficulty()
            ));
        }
    }

    @RabbitListener(queues = RabbitMQTopologyAchievements.ACHIEVEMENT_COMPLETED_QUEUE)
    void onAchievementCompletedMessageReceived(AchievementCompletedDto dto) {
        progressService.completeAchievement(dto.userId(), dto.gameId(), dto.code(), dto.timestamp());
    }
}

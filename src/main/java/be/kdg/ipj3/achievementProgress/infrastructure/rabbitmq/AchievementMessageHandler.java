package be.kdg.ipj3.achievementProgress.infrastructure.rabbitmq;

import be.kdg.ipj3.achievementProgress.application.AchievementProgressService;
import be.kdg.ipj3.achievementProgress.domain.GameContext;
import be.kdg.ipj3.achievementProgress.infrastructure.rabbitmq.config.RabbitMQTopologyAchievements;
import be.kdg.ipj3.achievementProgress.infrastructure.rabbitmq.dto.AchievementCompletedDto;
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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        progressService.completeAchievement(dto.userId(), dto.gameSessionId(), dto.code(), dto.timestamp());
    }
}

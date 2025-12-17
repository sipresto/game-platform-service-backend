package be.kdg.ipj3.achievementProgress.infrastructure.rabbitmq;

import be.kdg.ipj3.achievementProgress.application.AchievementProgressService;
import be.kdg.ipj3.achievementProgress.domain.GameContext;
import be.kdg.ipj3.achievementProgress.infrastructure.rabbitmq.config.RabbitMQTopologyAchievements;
import be.kdg.ipj3.achievementProgress.infrastructure.rabbitmq.dto.AchievementCompletedDto;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.sessions.shared.SessionStatus;
import be.kdg.ipj3.sessions.shared.SessionsApi;
import be.kdg.ipj3.shared.infrastructure.rabbitmq.dto.GameResultMessageDto;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AchievementMessageHandler {

    private final AchievementProgressService progressService;
    private final UsersApi usersApi;
    private final SessionsApi sessionsApi;

    public AchievementMessageHandler(AchievementProgressService progressService, UsersApi usersApi, SessionsApi sessionsApi) {
        this.progressService = progressService;
        this.usersApi = usersApi;
        this.sessionsApi = sessionsApi;
    }

    @RabbitListener(queues = RabbitMQTopologyAchievements.ACHIEVEMENT_GAME_RESULTS_QUEUE)
    void onGameResultMessageReceived(GameResultMessageDto dto) {
        GameId gameId = sessionsApi.findGameIdBySessionId(dto.gameId());
        UserId winnerId = dto.winnerId() != null
                ? usersApi.findIdByKeycloakId(dto.winnerId())
                : null;

        for (String player : dto.players()) {
            UserId userId = usersApi.findIdByKeycloakId(player);

            progressService.evaluateAchievementsAfterGame(new GameContext(
                    gameId,
                    userId,
                    winnerId,
                    SessionStatus.fromStringOrNull(dto.status()),
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

        UserId userId = usersApi.findIdByKeycloakId(dto.userId());
        GameId gameId = sessionsApi.findGameIdBySessionId(dto.gameId());

        progressService.completeAchievement(userId, gameId, dto.code(), dto.timestamp());
    }
}

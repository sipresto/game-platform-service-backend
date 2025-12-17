package be.kdg.ipj3.achievementProgress.application;

import be.kdg.ipj3.achievementProgress.api.dto.AchievementProgressDto;
import be.kdg.ipj3.achievementProgress.domain.AchievementProgress;
import be.kdg.ipj3.achievementProgress.domain.AchievementProgressRepository;
import be.kdg.ipj3.achievementProgress.domain.GameContext;
import be.kdg.ipj3.achievementProgress.domain.TriggerEvaluator;
import be.kdg.ipj3.achievementProgress.domain.strategies.CountingStrategyHandler;
import be.kdg.ipj3.catalog.shared.*;
import be.kdg.ipj3.notifications.shared.NotificationType;
import be.kdg.ipj3.notifications.shared.NotificationsApi;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AchievementProgressService {
    private final TriggerEvaluator triggerEvaluator;
    private final List<CountingStrategyHandler> strategyHandlers;
    private final AchievementProgressRepository progressRepository;
    private final GamesApi gamesApi;
    private final UsersApi usersApi;
    private final NotificationsApi notificationsApi;

    public AchievementProgressService(TriggerEvaluator triggerEvaluator, List<CountingStrategyHandler> strategyHandlers,
                                      AchievementProgressRepository progressRepository, GamesApi gamesApi, UsersApi usersApi,
                                      NotificationsApi notificationsApi) {
        this.triggerEvaluator = triggerEvaluator;
        this.strategyHandlers = strategyHandlers;
        this.progressRepository = progressRepository;
        this.gamesApi = gamesApi;
        this.usersApi = usersApi;
        this.notificationsApi = notificationsApi;
    }

    public void evaluateAchievementsAfterGame(GameContext context) {
        List<AchievementDto> gameAchievements = gamesApi.findAchievementDtosByGameId(context.game().id());

        if (!usersApi.existsById(context.player().id())) {
            return;
        }

        for (AchievementDto achievement : gameAchievements) {
            AchievementProgress progress = getProgress(new UserId(context.player().id()), achievement.id());

            if (progress.isCompleted() || !achievement.isCountedAfterGame()) {
                continue;
            }

            if (!triggerEvaluator.shouldTrigger(achievement.countTrigger(), context) &&
                    achievement.countingStrategy() != CountingStrategy.STREAK) {
                continue;
            }

            CountingStrategyHandler handler = strategyHandlers.stream()
                    .filter(h -> h.supports(achievement.countingStrategy()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No handler for strategy: " + achievement.countingStrategy()));

            AchievementProgress updatedProgress = handler.handle(achievement, progress, context);

            if (updatedProgress.isCompleted()) {
                usersApi.addPoints(updatedProgress.getPlayer().id(), achievement.points());
                notificationsApi.addNotification(achievement.name(), NotificationType.ACHIEVEMENT, context.player().id());
            }

            if (updatedProgress != progress) {
                progressRepository.save(updatedProgress);
            }
        }
    }

    public void completeAchievement(UserId userId, GameId gameId, String code, Instant timestamp) {
        AchievementDto achievement = gamesApi.findAchievementDtoByGameIdAndCode(gameId, code).orElse(null);
        if (achievement == null || !usersApi.existsById(userId.id())) {
            return;
        }

        AchievementProgress progress = getProgress(userId, achievement.id());

        if (progress.isCompleted()) {
            return;
        }

        AchievementProgress updatedProgress = progress.markCompleted(timestamp);
        if (updatedProgress.isCompleted()) {
            usersApi.addPoints(updatedProgress.getPlayer().id(), achievement.points());
            notificationsApi.addNotification(achievement.name(), NotificationType.ACHIEVEMENT, userId.id());
        }

        if (updatedProgress != progress) {
            progressRepository.save(updatedProgress);
        }
    }

    private AchievementProgress getProgress(UserId userId, AchievementId achievementId) {
        return progressRepository
                .findByUserIdAndAchievementId(userId, achievementId)
                .orElse(new AchievementProgress(userId, achievementId));
    }

    public List<AchievementProgressDto> findAllAchievementsByKeycloakId(String keycloakId) {
        var user = usersApi.findByKeycloakId(keycloakId);
        var userGameIds = usersApi.findOwnedGamesById(user.getId().id());
        if (userGameIds.isEmpty()) {
            return List.of();
        }

        List<List<GameAchievementDto>> achievements = new ArrayList<>();
        for (GameId gameId : userGameIds) {
            achievements.add(gamesApi.findGameAchievementDtosByIdWithAchievements(gameId.id()));
        }

        List<AchievementProgressDto> achievementProgressDtos = new ArrayList<>();

        for (List<GameAchievementDto> gameAchievementDtos : achievements) {
            for (GameAchievementDto gameAchievementDto : gameAchievementDtos) {
                var ap = progressRepository.findByUserIdAndAchievementId(user.getId(), gameAchievementDto.achievementId());
                if (ap.isPresent()) {
                    var progress = ap.get();
                    achievementProgressDtos.add(new AchievementProgressDto(
                            gameAchievementDto.gameId().id(),
                            gameAchievementDto.gameName(),
                            gameAchievementDto.achievementId().id(),
                            gameAchievementDto.achievementName(),
                            gameAchievementDto.achievementDescription(),
                            gameAchievementDto.requiredValue(),
                            progress.getCurrentProgress(),
                            progress.getCompletedAt()
                    ));
                }
                else {
                    achievementProgressDtos.add(new AchievementProgressDto(
                            gameAchievementDto.gameId().id(),
                            gameAchievementDto.gameName(),
                            gameAchievementDto.achievementId().id(),
                            gameAchievementDto.achievementName(),
                            gameAchievementDto.achievementDescription(),
                            gameAchievementDto.requiredValue(),
                            null,
                            null
                    ));
                }
            }
        }

        return achievementProgressDtos;
    }
}

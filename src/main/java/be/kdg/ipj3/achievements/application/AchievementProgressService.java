package be.kdg.ipj3.achievements.application;

import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.achievements.domain.AchievementProgressRepository;
import be.kdg.ipj3.achievements.domain.GameContext;
import be.kdg.ipj3.achievements.domain.TriggerEvaluator;
import be.kdg.ipj3.achievements.domain.strategies.CountingStrategyHandler;
import be.kdg.ipj3.catalog.shared.AchievementDto;
import be.kdg.ipj3.catalog.shared.AchievementId;
import be.kdg.ipj3.catalog.shared.CountingStrategy;
import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AchievementProgressService {
    private final TriggerEvaluator triggerEvaluator;
    private final List<CountingStrategyHandler> strategyHandlers;
    private final AchievementProgressRepository progressRepository;
    private final GamesApi gamesApi;
    private final UsersApi usersApi;

    public AchievementProgressService(TriggerEvaluator triggerEvaluator, List<CountingStrategyHandler> strategyHandlers,
                                      AchievementProgressRepository progressRepository, GamesApi gamesApi, UsersApi usersApi) {
        this.triggerEvaluator = triggerEvaluator;
        this.strategyHandlers = strategyHandlers;
        this.progressRepository = progressRepository;
        this.gamesApi = gamesApi;
        this.usersApi = usersApi;
    }

    public void evaluateAchievementsAfterGame(GameContext context) {
        List<AchievementDto> gameAchievements = gamesApi.findAchievementDtosByGameId(context.game());

        if (!usersApi.existsById(context.player())) {
            return;
        }

        for (AchievementDto achievement : gameAchievements) {
            AchievementProgress progress = getProgress(new UserId(context.player()), achievement.id());

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

            if (updatedProgress != progress) {
                progressRepository.save(updatedProgress);
            }
        }
    }

    public void completeAchievement(UUID userId, UUID gameSesssionId, String code, Instant timestamp) {
        // TODO: haal gameId uit lobby via gameSessionId -> zoek met die gameId en code de achievementId
        AchievementDto achievement = gamesApi.findAchievementDtoByCode(code).orElse(null);
        if (achievement == null || !usersApi.existsById(userId)) {
            return;
        }

        AchievementProgress progress = getProgress(new UserId(userId), achievement.id());

        if (progress.isCompleted()) {
            return;
        }

        AchievementProgress updatedProgress = progress.markCompleted(timestamp);

        if (updatedProgress != progress) {
            progressRepository.save(updatedProgress);
        }
    }

    private AchievementProgress getProgress(UserId userId, AchievementId achievementId) {
        return progressRepository
                .findByUserIdAndAchievementId(userId, achievementId)
                .orElse(new AchievementProgress(userId, achievementId));
    }
}

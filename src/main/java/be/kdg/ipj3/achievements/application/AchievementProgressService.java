package be.kdg.ipj3.achievements.application;

import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.achievements.domain.GameContext;
import be.kdg.ipj3.achievements.domain.IAchievementProgressRepository;
import be.kdg.ipj3.achievements.domain.TriggerEvaluator;
import be.kdg.ipj3.achievements.domain.strategies.CountingStrategyHandler;
import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.CountingStrategy;
import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.application.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AchievementProgressService {
    private final TriggerEvaluator triggerEvaluator;
    private final List<CountingStrategyHandler> strategyHandlers;
    private final IAchievementProgressRepository progressRepository;
    private final GameService gameService;
    private final UserService userService;

    public AchievementProgressService(TriggerEvaluator triggerEvaluator, List<CountingStrategyHandler> strategyHandlers,
                                      IAchievementProgressRepository progressRepository, GameService gameService, UserService userService) {
        this.triggerEvaluator = triggerEvaluator;
        this.strategyHandlers = strategyHandlers;
        this.progressRepository = progressRepository;
        this.gameService = gameService;
        this.userService = userService;
    }

    public void evaluateAchievementsAfterGame(GameContext context) {
        List<Achievement> gameAchievements = gameService.findAchievementsByGameId(context.game());

        for (Achievement achievement : gameAchievements) {
            AchievementProgress progress = getProgress(context.player(), achievement.getId());

            if (progress.isCompleted()) {
                continue;
            }

            if (!achievement.isCountedAfterGame()) {
                continue;
            }

            if (!triggerEvaluator.shouldTrigger(achievement.getCountTrigger(), context) &&
                    achievement.getCountingStrategy() != CountingStrategy.STREAK) {
                continue;
            }

            CountingStrategyHandler handler = strategyHandlers.stream()
                    .filter(h -> h.supports(achievement.getCountingStrategy()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No handler for strategy: " + achievement.getCountingStrategy()));

            AchievementProgress updatedProgress = handler.handle(achievement, progress, context);

            if (updatedProgress != progress) {
                progressRepository.save(updatedProgress);
            }
        }
    }

    public void completeAchievement(UserId userId, UUID gameSesssionId, String code, Instant timestamp) {
        // TODO: haal gameId uit lobby via gameSessionId -> zoek met die gameId en code de achievementId
        Achievement achievement = gameService.findAchievementByCode(code).orElse(null);
        if (achievement == null) {
            return;
        }

        try {
            userService.findById(userId);
        } catch (NotFoundException e) {
            return;
        }

        AchievementProgress progress = getProgress(userId, achievement.getId());

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

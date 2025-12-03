package be.kdg.ipj3.achievements.application;

import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.achievements.domain.GameContext;
import be.kdg.ipj3.achievements.domain.IAchievementProgressRepository;
import be.kdg.ipj3.achievements.domain.strategies.CountingStrategyHandler;
import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.shared.domain.CountingStrategy;
import be.kdg.ipj3.achievements.domain.TriggerEvaluator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementProgressService {
    private final TriggerEvaluator triggerEvaluator;
    private final List<CountingStrategyHandler> strategyHandlers;
    private final IAchievementProgressRepository progressRepository;
    private final GameService gameService;

    public AchievementProgressService(TriggerEvaluator triggerEvaluator, List<CountingStrategyHandler> strategyHandlers,
                                      IAchievementProgressRepository progressRepository, GameService gameService) {
        this.triggerEvaluator = triggerEvaluator;
        this.strategyHandlers = strategyHandlers;
        this.progressRepository = progressRepository;
        this.gameService = gameService;
    }

    public void evaluateAchievements(GameContext context) {
        List<Achievement> gameAchievements = gameService.findAchievementsByGameId(context.game());

        for (Achievement achievement : gameAchievements) {
            AchievementProgress progress = progressRepository
                    .findByUserIdAndAchievementId(context.player(), achievement.getId())
                    .orElse(new AchievementProgress(context.player(), achievement.getId()));

            if (progress.isCompleted()) {
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
}

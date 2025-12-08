package be.kdg.ipj3.achievements.domain.strategies;

import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.achievements.domain.GameContext;
import be.kdg.ipj3.achievements.domain.TriggerEvaluator;
import be.kdg.ipj3.catalog.shared.AchievementDto;
import be.kdg.ipj3.catalog.shared.CountingStrategy;
import org.springframework.stereotype.Component;

@Component
public class StreakStrategyHandler implements CountingStrategyHandler {
    private final TriggerEvaluator triggerEvaluator;

    public StreakStrategyHandler(TriggerEvaluator triggerEvaluator) {
        this.triggerEvaluator = triggerEvaluator;
    }

    @Override
    public boolean supports(CountingStrategy strategy) {
        return strategy == CountingStrategy.STREAK;
    }

    @Override
    public AchievementProgress handle(AchievementDto achievement,
                                      AchievementProgress currentProgress,
                                      GameContext context) {
        if (!triggerEvaluator.shouldTrigger(achievement.countTrigger(), context)) {
            return currentProgress.resetProgress();
        }

        return currentProgress.incrementProgress(1, achievement.requiredValue());
    }
}

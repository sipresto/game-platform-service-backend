package be.kdg.ipj3.achievements.domain.strategies;

import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.shared.domain.CountingStrategy;
import be.kdg.ipj3.achievements.domain.GameContext;
import be.kdg.ipj3.catalog.domain.Achievement;
import org.springframework.stereotype.Component;

@Component
public class ConditionStrategyHandler implements CountingStrategyHandler {

    @Override
    public boolean supports(CountingStrategy strategy) {
        return strategy == CountingStrategy.CONDITION;
    }

    @Override
    public AchievementProgress handle(Achievement achievement, AchievementProgress currentProgress, GameContext context) {
        if (!achievement.hasCustomRule()) {
            return currentProgress;
        }

        var result = achievement.getCustomRule().evaluate(context, currentProgress);

        if (result.shouldCount()) {
            if (result.metadata() == null) {
                return currentProgress.incrementProgress(result.incrementAmount(), achievement.getRequiredValue());
            }
            return currentProgress.incrementProgressWithMetadata(
                    result.incrementAmount(),
                    achievement.getRequiredValue(),
                    result.metadata()
            );
        }

        return currentProgress;
    }
}

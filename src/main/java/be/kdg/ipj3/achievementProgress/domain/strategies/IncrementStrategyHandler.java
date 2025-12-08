package be.kdg.ipj3.achievementProgress.domain.strategies;

import be.kdg.ipj3.achievementProgress.domain.AchievementProgress;
import be.kdg.ipj3.achievementProgress.domain.GameContext;
import be.kdg.ipj3.catalog.shared.AchievementDto;
import be.kdg.ipj3.catalog.shared.CountingStrategy;
import org.springframework.stereotype.Component;

@Component
public class IncrementStrategyHandler implements CountingStrategyHandler {

    @Override
    public boolean supports(CountingStrategy strategy) {
        return strategy == CountingStrategy.INCREMENT;
    }

    @Override
    public AchievementProgress handle(AchievementDto achievement, AchievementProgress currentProgress, GameContext context) {
        return currentProgress.incrementProgress(1, achievement.requiredValue());
    }
}

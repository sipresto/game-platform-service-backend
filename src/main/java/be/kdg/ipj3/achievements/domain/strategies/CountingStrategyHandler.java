package be.kdg.ipj3.achievements.domain.strategies;

import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.achievements.domain.GameContext;
import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.shared.CountingStrategy;

public interface CountingStrategyHandler {
    boolean supports(CountingStrategy strategy);

    AchievementProgress handle(Achievement achievement, AchievementProgress currentProgress, GameContext context);
}

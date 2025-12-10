package be.kdg.ipj3.catalog.shared;

import be.kdg.ipj3.catalog.domain.Achievement;

public record AchievementDto(
        AchievementId id,
        String name,
        Integer requiredValue,
        CountingStrategy countingStrategy,
        CountTrigger countTrigger,
        boolean isCountedAfterGame,
        int points
) {
    public static AchievementDto fromDomain(Achievement achievement) {
        return new AchievementDto(achievement.getId(), achievement.getName(), achievement.getRequiredValue(), achievement.getCountingStrategy(),
                achievement.getCountTrigger(), achievement.isCountedAfterGame(), achievement.getPoints());
    }
}

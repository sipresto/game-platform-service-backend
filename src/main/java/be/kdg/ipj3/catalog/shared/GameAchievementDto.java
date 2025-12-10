package be.kdg.ipj3.catalog.shared;

public record GameAchievementDto(
        GameId gameId,
        String gameName,
        AchievementId achievementId,
        String achievementName,
        String achievementDescription,
        Integer requiredValue
) {

}

package be.kdg.ipj3.achievementProgress.api.dto;

import java.time.Instant;
import java.util.UUID;

public record AchievementProgressDto(
        UUID gameId,
        String gameName,
        UUID achievementId,
        String achievementName,
        String achievementDescription,
        Integer requiredValue,
        Integer currentProgress,
        Instant completedAt

) {
}

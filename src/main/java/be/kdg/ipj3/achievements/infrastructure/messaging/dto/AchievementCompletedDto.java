package be.kdg.ipj3.achievements.infrastructure.messaging.dto;

import java.time.Instant;
import java.util.UUID;

public record AchievementCompletedDto(
        UUID userId,
        UUID gameId,
        String code,
        Instant timestamp
) {
}

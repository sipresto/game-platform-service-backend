package be.kdg.ipj3.achievementProgress.infrastructure.rabbitmq.dto;

import java.time.Instant;
import java.util.UUID;

public record AchievementCompletedDto(
        UUID userId,
        UUID gameSessionId,
        String code,
        Instant timestamp
) {
}

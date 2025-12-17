package be.kdg.ipj3.achievementProgress.infrastructure.rabbitmq.dto;

import java.time.Instant;
import java.util.UUID;

public record AchievementCompletedDto(
        String userId,
        UUID gameId,
        String code,
        Instant timestamp
) {
}

package be.kdg.ipj3.catalog.infrastructure.rabbitmq.dto;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.shared.CountTrigger;
import be.kdg.ipj3.catalog.shared.CountingStrategy;
import jakarta.validation.constraints.NotBlank;

public record RegisterAchievementDto(
        @NotBlank String name,
        @NotBlank String code,
        @NotBlank String description,
        Integer requiredValue,
        Integer points,
        String countingStrategy,
        String countTrigger
) {
    public Achievement toDomain() {
        int resolvedPoints = (points != null)
                ? points
                : 10;

        return new Achievement(
                name,
                code,
                description,
                requiredValue,
                resolvedPoints,
                CountingStrategy.fromStringOrNull(countingStrategy),
                CountTrigger.fromStringOrNull(countTrigger)
        );
    }
}
package be.kdg.ipj3.catalog.infrastructure.messaging.dto;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.shared.domain.CountTrigger;
import be.kdg.ipj3.shared.domain.CountingStrategy;
import jakarta.validation.constraints.NotBlank;

public record AddAchievementDto(
        @NotBlank String name,
        @NotBlank String code,
        @NotBlank String description,
        Integer requiredValue,
        Integer points,
        CountingStrategy countingStrategy,
        CountTrigger countTrigger
) {
    Achievement toDomain() {
        int resolvedPoints = (points != null)
                ? points
                : 10;

        return new Achievement(
                name,
                code,
                description,
                requiredValue,
                resolvedPoints,
                countingStrategy,
                countTrigger
        );
    }
}
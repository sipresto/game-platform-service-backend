package be.kdg.ipj3.shared.domain.game;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record AchievementName(@NotNull String achievementName) {
    public AchievementName{
        if(achievementName == null || achievementName.isBlank() || achievementName.length() < 5 || achievementName.length() > 100)
            throw new IllegalArgumentException("Achievement name cannot be null or blank or smaller than 5 characters or larger than 100 characters!");
    }
}

package be.kdg.ipj3.shared.domain.game;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record AchievementId(@NotNull UUID achievementId) {
    public AchievementId{
        Objects.requireNonNull(achievementId, "Achievement id cannot be null");
    }
}

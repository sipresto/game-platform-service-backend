package be.kdg.ipj3.catalog.shared;

import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record AchievementId(UUID id) {
    public AchievementId {
        Objects.requireNonNull(id, "Achievement id cannot be null");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Achievement [" + id + "] not found");
    }

    public static AchievementId create() {
        return new AchievementId(UUID.randomUUID());
    }
}

package be.kdg.ipj3.shared.domain.game;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record GameDescription(@NotNull String description) {
    public GameDescription{
        if(description == null || description.isBlank() || description.length() < 20 || description.length() > 500)
            throw new IllegalArgumentException("Description cannot be null or blank and needs to be more than 20 characters or larger than 500 characters");
    }
}

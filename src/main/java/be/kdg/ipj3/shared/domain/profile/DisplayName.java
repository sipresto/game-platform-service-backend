package be.kdg.ipj3.shared.domain.profile;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record DisplayName(@NotNull String displayName) {
    public DisplayName{
        if(displayName == null || displayName.isBlank() || displayName.length() < 3 || displayName.length() > 75)
            throw new IllegalArgumentException("displayName cannot be null or empty and has to be between 3 and 75 characters");
    }
}

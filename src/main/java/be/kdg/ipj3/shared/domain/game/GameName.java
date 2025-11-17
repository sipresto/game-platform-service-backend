package be.kdg.ipj3.shared.domain.game;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record GameName(@NotNull String gameName) {
    public GameName {
        if(gameName == null || gameName.isBlank() || gameName.length() < 3 || gameName.length() > 100)
            throw new IllegalArgumentException("gameName cannot be null or blank and needs to be more than 3 characters or larger than 100 characters!");
    }
}

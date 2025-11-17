package be.kdg.ipj3.session.domain.lobby;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record MaxPlayers(@NotNull int maxPlayers) {
    public MaxPlayers{
        if(maxPlayers <= 0)
            throw new IllegalArgumentException("maxPlayers must be positive and larger than 0!");
    }
}

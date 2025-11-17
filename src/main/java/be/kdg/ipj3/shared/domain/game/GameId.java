package be.kdg.ipj3.shared.domain.game;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record GameId(@NotNull UUID gameId) {
    public GameId{
        Objects.requireNonNull(gameId, "gameId cannot be null");
    }
}

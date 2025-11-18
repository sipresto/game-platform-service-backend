package be.kdg.ipj3.shared.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record GameId(UUID id) {
    public GameId{
        Objects.requireNonNull(id, "Game id cannot be null");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Game [" + id + "] not found");
    }

    public static GameId create() {
        return new GameId(UUID.randomUUID());
    }
}

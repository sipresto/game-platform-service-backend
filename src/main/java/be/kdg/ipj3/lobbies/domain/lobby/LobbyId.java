package be.kdg.ipj3.lobbies.domain.lobby;

import be.kdg.ipj3.shared.domain.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record LobbyId(UUID id) {
    public LobbyId {
        Objects.requireNonNull(id, "Lobby id cannot be null");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Lobby [" + id + "] not found");
    }

    public static LobbyId create() {
        return new LobbyId(UUID.randomUUID());
    }
}

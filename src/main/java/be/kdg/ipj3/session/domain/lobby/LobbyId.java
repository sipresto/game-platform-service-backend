package be.kdg.ipj3.session.domain.lobby;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record LobbyId(@NotNull UUID lobbyId) {
    public LobbyId {
        Objects.requireNonNull(lobbyId);
    }
}

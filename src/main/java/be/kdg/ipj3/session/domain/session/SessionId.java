package be.kdg.ipj3.session.domain.session;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record SessionId(@NotNull UUID sessionId) {
    public SessionId {
        Objects.requireNonNull(sessionId, "sessionId cannot be null!");
    }
}

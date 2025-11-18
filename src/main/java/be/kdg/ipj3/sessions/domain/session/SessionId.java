package be.kdg.ipj3.sessions.domain.session;

import be.kdg.ipj3.shared.domain.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record SessionId(UUID id) {
    public SessionId {
        Objects.requireNonNull(id, "Session id cannot be null!");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Session [" + id + "] not found");
    }

    public static SessionId create() {
        return new SessionId(UUID.randomUUID());
    }
}

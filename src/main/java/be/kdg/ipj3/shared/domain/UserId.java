package be.kdg.ipj3.shared.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record UserId(UUID id) {
    public UserId{
        Objects.requireNonNull(id, "User id cannot be null");
    }

    public NotFoundException notFound() {
        return new NotFoundException("User [" + id + "] not found");
    }

    public static UserId create() {
        return new UserId(UUID.randomUUID());
    }
}

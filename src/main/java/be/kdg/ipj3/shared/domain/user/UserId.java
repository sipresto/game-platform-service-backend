package be.kdg.ipj3.shared.domain.user;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record UserId(@NotNull UUID userId) {
    public UserId{
        Objects.requireNonNull(userId, "UserId cannot be null");
    }
}

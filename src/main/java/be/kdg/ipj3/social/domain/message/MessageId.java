package be.kdg.ipj3.social.domain.message;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record MessageId(@NotNull UUID messageId) {
    public MessageId {
        Objects.requireNonNull(messageId, "MessageId cannot be null");
    }
}

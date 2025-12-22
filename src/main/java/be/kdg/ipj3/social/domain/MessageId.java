package be.kdg.ipj3.social.domain;

import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record MessageId(UUID id) {
    public MessageId {
        Objects.requireNonNull(id, "Message id cannot be null");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Message [" + id + "] not found");
    }

    public static MessageId create() {
        return new MessageId(UUID.randomUUID());
    }
}

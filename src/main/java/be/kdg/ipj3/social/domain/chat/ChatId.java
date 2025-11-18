package be.kdg.ipj3.social.domain.chat;

import be.kdg.ipj3.shared.domain.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record ChatId(UUID id) {
    public ChatId {
        Objects.requireNonNull(id, "Chat id cannot be null");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Chat [" + id + "] not found");
    }

    public static ChatId create() {
        return new ChatId(UUID.randomUUID());
    }
}

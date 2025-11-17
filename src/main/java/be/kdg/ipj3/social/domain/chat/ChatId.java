package be.kdg.ipj3.social.domain.chat;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public record ChatId(@NotNull UUID id) {
    public ChatId {
        Objects.requireNonNull(id, "ChatId cannot be null");
    }
}

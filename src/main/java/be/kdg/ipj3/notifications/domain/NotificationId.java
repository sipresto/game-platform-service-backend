package be.kdg.ipj3.notifications.domain;

import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record NotificationId(UUID id) {
    public NotificationId {
        Objects.requireNonNull(id, "Notification id cannot be null!");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Notification [" + id + "] not found");
    }

    public static NotificationId create() {
        return new NotificationId(UUID.randomUUID());
    }
}

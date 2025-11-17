package be.kdg.ipj3.shared.domain.profile;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record NotificationId(@NotNull UUID notificationId) {
    public NotificationId{
        Objects.requireNonNull(notificationId, "notificationId cannot be null!");
    }
}

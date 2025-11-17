package be.kdg.ipj3.shared.domain.user;

import jakarta.validation.constraints.NotNull;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record UserName(@NotNull String username) {
    public UserName {
        if (username == null || username.isBlank() || username.length() < 3)
            throw new IllegalArgumentException("Username cannot be null or blank and needs to be larger then 3 characters");
    }
}

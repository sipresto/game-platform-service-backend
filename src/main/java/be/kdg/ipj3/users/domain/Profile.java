package be.kdg.ipj3.users.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public record Profile (String displayName, String bio) {
    public Profile {
        Objects.requireNonNull(displayName);
    }
}

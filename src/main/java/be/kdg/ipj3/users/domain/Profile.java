package be.kdg.ipj3.users.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public record Profile(String displayName, String bio, byte[] profileImage, String imageMimeType) {
    public Profile {
        Objects.requireNonNull(displayName);
    }

    public Profile(String displayName, String bio) {
        this(displayName, bio, null, null);
    }
}
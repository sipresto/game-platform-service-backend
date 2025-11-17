package be.kdg.ipj3.shared.domain.profile;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum Visibility {
    PRIVATE,
    FRIENDS_ONLY,
    PUBLIC
}

package be.kdg.ipj3.users.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum Visibility {
    PRIVATE,
    FRIENDS_ONLY,
    PUBLIC
}

package be.kdg.ipj3.users.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum VisibleField {
    PROFILE,
    POINTS,
    FAVORITE_GAMES,
    OWNED_GAMES,
    FRIENDS_LIST
}
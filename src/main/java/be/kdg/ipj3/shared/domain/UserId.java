package be.kdg.ipj3.shared.domain;

import java.util.Objects;

public record UserId(String value) {

    public static UserId fromKeycloak(String keycloakId) {
        if (keycloakId == null || keycloakId.isBlank()) {
            throw new IllegalArgumentException("Keycloak ID cannot be null or empty");
        }
        return new UserId(keycloakId);
    }
}
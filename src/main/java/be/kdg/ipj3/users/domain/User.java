package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
public class User {
    @Identity
    private final UserId id;
    private String username;

    public User(UserId id, String username) {
        this.id = id;
        this.username = username;
    }

    public static User createFromKeycloak(String keycloakId, String keycloakUsername) {
        return new User(UserId.fromKeycloak(keycloakId), keycloakUsername);
    }

    public UserId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void updateUsername(String newUsername) {
        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = newUsername;
    }
}
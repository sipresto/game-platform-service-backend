package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.List;

@AggregateRoot
public class User {
    @Identity
    private final UserId id;
    private final String keycloakId;
    private String username;
    private List<GameId> ownedGames;
    private Profile profile;

    public User(UserId id, String keycloakId, String username, List<GameId> ownedGames, Profile profile) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.ownedGames = ownedGames;
        this.profile = profile;
    }

    public User(UserId id, String keycloakId, String username, Profile profile) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.profile = profile;
    }

    public static User createFromKeycloak(String keycloakId, String keycloakUsername) {
        return new User(UserId.create(), keycloakId, keycloakUsername, List.of(), new Profile(keycloakUsername, ""));
    }

    public UserId getId() {
        return id;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public String getUsername() {
        return username;
    }

    public List<GameId> getOwnedGames() {
        return ownedGames;
    }

    public Profile getProfile() {
        return profile;
    }

    public void updateUsername(String newUsername) {
        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = newUsername;
        this.profile = this.profile.withDisplayName(newUsername);
    }

    public void addGame(GameId gameId) {
        ownedGames.add(gameId);
    }
}
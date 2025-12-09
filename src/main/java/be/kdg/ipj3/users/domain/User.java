package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;

@AggregateRoot
public class User {
    @Identity
    private final UserId id;
    private final String keycloakId;
    private String username;
    private final List<GameId> ownedGames;
    private int points;
    private Profile profile;
    private Visibility visibility;
    private final List<UserId> friends;

    public User(UserId id, String keycloakId, String username, List<GameId> ownedGames, int points, Profile profile) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.ownedGames = ownedGames;
        this.points = points;
        this.profile = profile;
        this.friends = new ArrayList<>();
        this.visibility = Visibility.PUBLIC;
    }

    public User(UserId id, String keycloakId, String username, Profile profile, int points) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.ownedGames = new ArrayList<>();
        this.points = points;
        this.profile = profile;
        this.visibility = Visibility.PUBLIC;
        this.friends = new ArrayList<>();
    }

    public static User createFromKeycloak(String keycloakId, String keycloakUsername) {
        return new User(UserId.create(), keycloakId, keycloakUsername, List.of(), 0, new Profile(keycloakUsername, ""));
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

    public void updateProfile(Profile newProfile) {
        String displayName = newProfile.displayName() == null || newProfile.displayName().isBlank()
                ? profile.displayName()
                : newProfile.displayName();

        String bio = newProfile.bio() == null
                ? profile.bio()
                : newProfile.bio();

        this.profile = new Profile(displayName, bio);
        this.username = displayName;
    }

    public void updateVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void addGame(GameId gameId) {
        if (!ownedGames.contains(gameId)) {
            ownedGames.add(gameId);
        } else {
            throw new IllegalArgumentException("Game already owned by this user");
        }
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public List<UserId> getFriends() {
        return friends;
    }

    public int getPoints() {
        return points;
    }

    public void addFriend(UserId userId) {
        if (!friends.contains(userId)) {
            friends.add(userId);
        } else {
            throw new IllegalArgumentException("You are already befriended with this user");
        }
    }

    public void addPoints(int points) {
        this.points += points;
    }
}
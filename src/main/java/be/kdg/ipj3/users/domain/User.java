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
    private final List<GameId> favoriteGames;
    private int points;
    private Profile profile;
    private Visibility visibility;
    private final List<UserId> friends;
    private final String email;
    private final List<FriendRequest> friendRequests;

    public User(UserId id, String keycloakId, String username, List<GameId> ownedGames, List<GameId> favoriteGames, int points, Profile profile, String email) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.ownedGames = ownedGames != null ? new ArrayList<>(ownedGames) : new ArrayList<>();
        this.favoriteGames = favoriteGames != null ? new ArrayList<>(favoriteGames) : new ArrayList<>();
        this.points = points;
        this.profile = profile;
        this.friends = new ArrayList<>();
        this.visibility = Visibility.PUBLIC;
        this.email = email;
        this.friendRequests = new ArrayList<>();
    }

    public User(UserId id, String keycloakId, String username, Profile profile, int points, String email) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.ownedGames = new ArrayList<>();
        this.favoriteGames = new ArrayList<>();
        this.points = points;
        this.profile = profile;
        this.visibility = Visibility.PUBLIC;
        this.friends = new ArrayList<>();
        this.email = email;
        this.friendRequests = new ArrayList<>();
    }

    public static User createFromKeycloak(String keycloakId, String keycloakUsername, String email) {
        return new User(UserId.create(), keycloakId, keycloakUsername,
                List.of(),
                List.of(),
                0, new Profile(keycloakUsername, ""), email);
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
    public void addFavorite(GameId gameId){
        if (!favoriteGames.contains(gameId)){
            favoriteGames.add(gameId);
        } else {
            throw new IllegalArgumentException("Game already favorited by this user");
        }
    }
    public void removeFavorite(GameId gameId){
        if (favoriteGames.contains(gameId)){
            favoriteGames.remove(gameId);
        } else {
            throw new IllegalArgumentException("Game not favorited by this user");
        }
    }


    public List<GameId> getFavoriteGames() {
        return favoriteGames;
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

    public void addFriendRequest(FriendRequest friendRequest) {
        this.friendRequests.add(friendRequest);
    }

    public void removeFriendRequest(UserId userId) {
        this.friendRequests.remove(friendRequests.stream().filter(friendRequest -> friendRequest.sender().equals(userId)).findFirst().orElse(null));
    }

    public List<FriendRequest> getFriendRequests() {
        return friendRequests;
    }

    public void acceptFriendRequest(UserId userId) {
        FriendRequest request = friendRequests.stream()
                .filter(friendRequest -> friendRequest.sender().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No friend request from this user"));

        this.addFriend(request.sender());
        this.removeFriendRequest(userId);
    }

    public String getEmail() {
        return email;
    }

    public void updateProfileImage(byte[] image, String mimeType) {
        this.profile = new Profile(
                profile.displayName(),
                profile.bio(),
                image,
                mimeType
        );
    }

    public void removeProfileImage() {
        this.profile = new Profile(
                profile.displayName(),
                profile.bio(),
                null,
                null
        );
    }
}
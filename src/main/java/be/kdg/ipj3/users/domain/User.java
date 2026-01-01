package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private VisibilitySettings visibilitySettings;
    private final List<UserId> friends;
    private final String email;
    private final List<FriendRequest> incomingFriendRequests;
    private final List<FriendRequest> outgoingFriendRequests;
    private final List<UserId> blockedUsers;

    public User(UserId id, String keycloakId, String username, List<GameId> ownedGames,
                List<GameId> favoriteGames, int points, Profile profile,
                VisibilitySettings visibilitySettings, String email) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.ownedGames = ownedGames != null ? new ArrayList<>(ownedGames) : new ArrayList<>();
        this.favoriteGames = favoriteGames != null ? new ArrayList<>(favoriteGames) : new ArrayList<>();
        this.points = points;
        this.profile = profile;
        this.visibilitySettings = visibilitySettings;
        this.friends = new ArrayList<>();
        this.email = email;
        this.incomingFriendRequests = new ArrayList<>();
        this.outgoingFriendRequests = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
    }

    public User(UserId id, String keycloakId, String username, Profile profile, int points, String email) {
        this(id, keycloakId, username,
                new ArrayList<>(), new ArrayList<>(),
                points, profile, VisibilitySettings.defaultPublic(), email);
    }

    public User(UserId id, String keycloakId, String username, List<GameId> ownedGames,
                List<GameId> favoriteGames, int points, Profile profile, String email) {
        this(id, keycloakId, username, ownedGames, favoriteGames, points, profile,
                VisibilitySettings.defaultPublic(), email);
    }

    public static User createFromKeycloak(String keycloakId, String keycloakUsername, String email) {
        return new User(UserId.create(), keycloakId, keycloakUsername,
                List.of(),
                List.of(),
                0, new Profile(keycloakUsername, ""), email);
    }

    public Integer getVisiblePoints(User viewer) {
        if (viewer != null && this.id.equals(viewer.getId())) {
            return this.points;
        }
        if (visibilitySettings.isVisibleTo(VisibleField.POINTS, viewer, this)) {
            return this.points;
        }
        return null;
    }

    public List<GameId> getVisibleFavoriteGames(User viewer) {
        if (viewer != null && this.id.equals(viewer.getId())) {
            return new ArrayList<>(this.favoriteGames);
        }
        if (visibilitySettings.isVisibleTo(VisibleField.FAVORITE_GAMES, viewer, this)) {
            return new ArrayList<>(this.favoriteGames);
        }
        return null;
    }

    public List<GameId> getVisibleOwnedGames(User viewer) {
        if (viewer != null && this.id.equals(viewer.getId())) {
            return new ArrayList<>(this.ownedGames);
        }
        if (visibilitySettings.isVisibleTo(VisibleField.OWNED_GAMES, viewer, this)) {
            return new ArrayList<>(this.ownedGames);
        }
        return null;
    }

    public List<UserId> getVisibleFriends(User viewer) {
        if (viewer != null && this.id.equals(viewer.getId())) {
            return new ArrayList<>(this.friends);
        }
        if (visibilitySettings.isVisibleTo(VisibleField.FRIENDS_LIST, viewer, this)) {
            return new ArrayList<>(this.friends);
        }
        return null;
    }

    public Profile getVisibleProfile(User viewer) {
        if (viewer != null && this.id.equals(viewer.getId())) {
            return this.profile;
        }

        boolean profileVisible = visibilitySettings.isVisibleTo(VisibleField.PROFILE, viewer, this);
        if (profileVisible) {
            return this.profile;
        } else {
            return new Profile(this.profile.displayName(), null, null, null);
        }
    }

    public VisibilitySettings getVisibilitySettings() {
        return visibilitySettings;
    }

    public void updateVisibility(Visibility visibility) {
        this.visibilitySettings = VisibilitySettings.template(visibility);
    }

    public void updateVisibilityForField(VisibleField field, Visibility visibility) {
        this.visibilitySettings = visibilitySettings.withFieldVisibility(field, visibility);
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
        return new ArrayList<>(ownedGames);
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
        return new ArrayList<>(favoriteGames);
    }

    public List<UserId> getFriends() {
        return new ArrayList<>(friends);
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

    public void addIncomingFriendRequest(FriendRequest friendRequest) {
        this.incomingFriendRequests.add(friendRequest);
    }

    public void addOutgoingFriendRequest(FriendRequest friendRequest) {
        this.outgoingFriendRequests.add(friendRequest);
    }

    public void removeOutgoingFriendRequest(UserId userId) {
        this.outgoingFriendRequests.remove(outgoingFriendRequests.stream()
                .filter(friendRequest -> friendRequest.otherUser().equals(userId))
                .findFirst()
                .orElse(null));
    }

    public void removeIncomingFriendRequest(UserId userId) {
        this.incomingFriendRequests.remove(incomingFriendRequests.stream()
                .filter(friendRequest -> friendRequest.otherUser().equals(userId))
                .findFirst()
                .orElse(null));
    }

    public List<FriendRequest> getIncomingFriendRequests() {
        return new ArrayList<>(incomingFriendRequests);
    }

    public void acceptFriendRequest(UserId userId) {
        FriendRequest request = incomingFriendRequests.stream()
                .filter(friendRequest -> friendRequest.otherUser().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No friend request from this user"));

        this.addFriend(request.otherUser());
        this.removeIncomingFriendRequest(userId);
    }

    public void validateFriendRequests(UserId userId) {
        if(this.id.equals(userId) || this.outgoingFriendRequests.stream()
                .anyMatch(friendRequest -> friendRequest.otherUser().equals(userId))) {
            throw new IllegalArgumentException("You cannot send friend requests to yourself nor to users you have already sent a request to");
        }
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

    public List<FriendRequest> getOutgoingFriendRequests() {
        return new ArrayList<>(outgoingFriendRequests);
    }

    public void removeFriend(UserId id) {
        if (!friends.contains(id)) {
            throw new IllegalArgumentException("You aren't friends with this user");
        }
        else {
            friends.remove(id);
        }
    }

    public List<UserId> getBlockedUsers() {
        return new ArrayList<>(blockedUsers);
    }

    public void block(UserId userId) {
        handleBlock(userId);
        blockedUsers.add(userId);
    }

    public void unblock(UserId unblockId) {
        if (blockedUsers.contains(unblockId)) {
            blockedUsers.remove(unblockId);
        }
        else {
            throw new IllegalArgumentException("This user is not blocked");
        }
    }

    public void getBlocked(UserId blockerId) {
        handleBlock(blockerId);
    }

    public void handleBlock(UserId blockerId) {
        if (friends.contains(blockerId)) {
            removeFriend(blockerId);
        }
        if (incomingFriendRequests.stream().map(FriendRequest::otherUser).anyMatch(userId1 -> userId1 == blockerId)) {
            removeIncomingFriendRequest(blockerId);
        }
        if (outgoingFriendRequests.stream().map(FriendRequest::otherUser).anyMatch(userId1 -> userId1 == blockerId)) {
            removeOutgoingFriendRequest(blockerId);
        }
    }
}
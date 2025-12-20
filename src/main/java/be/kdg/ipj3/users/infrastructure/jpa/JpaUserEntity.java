package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class JpaUserEntity {

    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column(nullable = false)
    private String keycloakId;

    @Column(nullable = false)
    private String username;

    @Column
    private int points;

    @Column
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @Column
    private String email;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "owned_games",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "game_id")
    private final List<UUID> ownedGames = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "favorite_games",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "game_id")
    private final List<UUID> favoriteGames = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "friend_id")
    private final List<UUID> friends = new ArrayList<>();

    private JpaProfile profile;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "friend_requests",
            joinColumns = @JoinColumn(name = "user_id")
    )
    private final List<JpaFriendRequest> friendRequests = new ArrayList<>();

    protected JpaUserEntity() {
    } // for JPA

    public JpaUserEntity(UUID id, String keycloakId, String username, JpaProfile profile, int points, Visibility visibility, String email) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.profile = profile;
        this.points = points;
        this.visibility = visibility;
        this.email = email;
    }

    public static JpaUserEntity fromDomain(User user) {
        JpaUserEntity jpaUserEntity = new JpaUserEntity(
                user.getId().id(),
                user.getKeycloakId(),
                user.getUsername(),
                JpaProfile.fromDomain(user.getProfile()),
                user.getPoints(),
                user.getVisibility(),
                user.getEmail()
        );

        user.getOwnedGames().stream()
                .map(GameId::id)
                .forEach(jpaUserEntity::addGame);

        user.getFavoriteGames().stream()
                .map(GameId::id)
                .forEach(jpaUserEntity::addFavoriteGame);

        user.getFriends().stream()
                .map(UserId::id)
                .forEach(jpaUserEntity::addFriend);

        user.getFriendRequests().stream()
                .map(JpaFriendRequest::fromDomain)
                .forEach(jpaUserEntity.friendRequests::add);

        return jpaUserEntity;
    }

    public User toDomain() {
        User user = new User(new UserId(id), keycloakId, username, profile.toDomain(), points, email);
        ownedGames.stream()
                .map(GameId::new)
                .forEach(user::addGame);

        favoriteGames.stream()
                .map(GameId::new)
                .forEach(user::addFavorite);

        friends.stream()
                .map(UserId::new)
                .forEach(user::addFriend);

        friendRequests.stream()
                .map(JpaFriendRequest::toDomain)
                .forEach(user::addFriendRequest);

        user.updateVisibility(this.visibility);
        return user;
    }

    private void addGame(UUID gameId) {
        this.ownedGames.add(gameId);
    }
    private void addFavoriteGame(UUID gameId){
        this.favoriteGames.add(gameId);
    }

    private void addFriend(UUID friendId) {
        this.friends.add(friendId);
    }

    public UUID getId() {
        return id;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public String getUsername() {
        return username;
    }

    public List<UUID> getOwnedGames() {
        return ownedGames;
    }

    public List<UUID> getFriends() {
        return friends;
    }

    public JpaProfile getProfile() {
        return profile;
    }

    public Visibility getVisibility() {
        return visibility;
    }
}

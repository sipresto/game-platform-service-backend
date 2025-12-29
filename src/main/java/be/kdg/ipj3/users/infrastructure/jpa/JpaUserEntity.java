package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.perks.shared.PerkId;
import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

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
    private String email;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_unlocked_perks",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "perk_id")
    private Set<UUID> unlockedPerks = new HashSet<>();

    @Column(name = "equipped_border_id")
    private UUID equippedBorderId;

    @Column(name = "equipped_theme_id")
    private UUID equippedThemeId;

    @Column(name = "equipped_title_id")
    private UUID equippedTitleId;

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
            name = "friend_requests_incoming",
            joinColumns = @JoinColumn(name = "user_id")
    )
    private final List<JpaFriendRequest> incomingFriendRequests = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "friend_requests_outgoing",
            joinColumns = @JoinColumn(name = "user_id")
    )
    private final List<JpaFriendRequest> outgoingFriendRequests = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_visibility_settings",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @MapKeyColumn(name = "field")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private Map<VisibleField, Visibility> visibilitySettings = new HashMap<>();

    protected JpaUserEntity() {
    } // for JPA

    public JpaUserEntity(UUID id, String keycloakId, String username, JpaProfile profile,
                         int points, String email, Map<VisibleField, Visibility> visibilitySettings,
                         Set<UUID> unlockedPerks, UUID equippedBorderId, UUID equippedThemeId, UUID equippedTitleId) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.profile = profile;
        this.points = points;
        this.email = email;
        this.visibilitySettings = visibilitySettings != null ? new HashMap<>(visibilitySettings) : new HashMap<>();
        this.unlockedPerks = new HashSet<>(unlockedPerks);
        this.equippedBorderId = equippedBorderId;
        this.equippedThemeId = equippedThemeId;
        this.equippedTitleId = equippedTitleId;
    }

    public static JpaUserEntity fromDomain(User user) {
        Map<VisibleField, Visibility> visibilityMap = user.getVisibilitySettings().fieldVisibilities();
        UserCosmetics cosmetics = user.getCosmetics();

        Set<UUID> unlockedIds = cosmetics.getUnlockedPerkIds().stream()
                .map(PerkId::id)
                .collect(Collectors.toSet());

        UUID borderId = cosmetics.getEquippedBorderId() != null ? cosmetics.getEquippedBorderId().id() : null;
        UUID themeId = cosmetics.getEquippedThemeId() != null ? cosmetics.getEquippedThemeId().id() : null;
        UUID titleId = cosmetics.getEquippedTitleId() != null ? cosmetics.getEquippedTitleId().id() : null;

        JpaUserEntity jpaUserEntity = new JpaUserEntity(
                user.getId().id(),
                user.getKeycloakId(),
                user.getUsername(),
                JpaProfile.fromDomain(user.getProfile()),
                user.getPoints(),
                user.getEmail(),
                visibilityMap,
                unlockedIds,
                borderId,
                themeId,
                titleId
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

        user.getIncomingFriendRequests().stream()
                .map(JpaFriendRequest::fromDomain)
                .forEach(jpaUserEntity.incomingFriendRequests::add);

        user.getOutgoingFriendRequests().stream()
                .map(JpaFriendRequest::fromDomain)
                .forEach(jpaUserEntity.outgoingFriendRequests::add);

        return jpaUserEntity;
    }

    public User toDomain() {
        VisibilitySettings visibilitySettings = new VisibilitySettings(new HashMap<>(this.visibilitySettings));

        UserCosmetics cosmetics = new UserCosmetics();

        if (this.unlockedPerks != null) {
            this.unlockedPerks.forEach(uuid -> cosmetics.unlockPerk(new PerkId(uuid)));
        }

        if (this.equippedBorderId != null) {
            cosmetics.equipBorder(new PerkId(this.equippedBorderId));
        }
        if (this.equippedThemeId != null) {
            cosmetics.equipTheme(new PerkId(this.equippedThemeId));
        }
        if (this.equippedTitleId != null) {
            cosmetics.equipTitle(new PerkId(this.equippedTitleId));
        }

        User user = new User(
                new UserId(id),
                keycloakId,
                username,
                new ArrayList<>(),
                new ArrayList<>(),
                cosmetics,
                points,
                profile.toDomain(),
                visibilitySettings,
                email
        );

        ownedGames.stream()
                .map(GameId::new)
                .forEach(user::addGame);

        favoriteGames.stream()
                .map(GameId::new)
                .forEach(user::addFavorite);

        friends.stream()
                .map(UserId::new)
                .forEach(user::addFriend);

        incomingFriendRequests.stream()
                .map(JpaFriendRequest::toDomain)
                .forEach(user::addIncomingFriendRequest);

        outgoingFriendRequests.stream()
                .map(JpaFriendRequest::toDomain)
                .forEach(user::addOutgoingFriendRequest);

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

    public Map<VisibleField, Visibility> getVisibilitySettings() {
        return visibilitySettings;
    }
}
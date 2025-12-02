package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
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
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "owned_games",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "game_id")
    private List<UUID> ownedGames = new ArrayList<>();

    private JpaProfile profile;

    protected JpaUserEntity() {
    } // for JPA

    public JpaUserEntity(UUID id, String keycloakId, String username, JpaProfile profile, Visibility visibility) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.profile = profile;
        this.visibility = visibility;
    }

    public static JpaUserEntity fromDomain(User user) {
        JpaUserEntity jpaUserEntity = new JpaUserEntity(user.getId().id(), user.getKeycloakId(),
                user.getUsername(), JpaProfile.fromDomain(user.getProfile()), user.getVisibility());

        user.getOwnedGames().stream()
                .map(GameId::id)
                .forEach(jpaUserEntity::addGame);

        return jpaUserEntity;
    }

    public User toDomain() {
        User user = new User(new UserId(id), keycloakId, username, profile.toDomain());

        ownedGames.stream()
                .map(GameId::new)
                .forEach(user::addGame);

        user.updateVisibility(this.visibility);
        return user;
    }

    private void addGame(UUID gameId) {
        this.ownedGames.add(gameId);
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

    public JpaProfile getProfile() {
        return profile;
    }
}
package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class JpaUserEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    private String username;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "owned_games",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "game_id")
    private List<UUID> ownedGames = new ArrayList<>();

    protected JpaUserEntity() {}

    public JpaUserEntity(String userId, String username, List<GameId> ownedGames) {
        this.userId = userId;
        this.username = username;

        if (ownedGames != null) {
            this.ownedGames = ownedGames.stream()
                    .map(GameId::id)
                    .collect(Collectors.toList());
        } else {
            this.ownedGames = new ArrayList<>();
        }
    }

    public static JpaUserEntity fromDomain(User user) {
        return new JpaUserEntity(
                user.getId().value(),
                user.getUsername(),
                user.getOwnedGames()
        );
    }

    public User toDomain() {
        List<GameId> domainOwnedGames = new ArrayList<>();
        if (this.ownedGames != null) {
            domainOwnedGames = this.ownedGames.stream()
                    .map(GameId::new)
                    .collect(Collectors.toList());
        }

        return new User(
                new UserId(this.userId),
                this.username,
                domainOwnedGames
        );
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public List<UUID> getOwnedGames() {
        return ownedGames;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
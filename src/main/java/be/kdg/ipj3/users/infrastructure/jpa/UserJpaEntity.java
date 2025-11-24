package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    private String username;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "owned_games",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "game_id")
    private List<UUID> ownedGames = new ArrayList<>();

    protected UserJpaEntity() {} // for JPA

    public UserJpaEntity(UUID userId, String username, List<GameId> ownedGames) {
        this.userId = userId;
        this.username = username;
        if (ownedGames != null) {
            for (GameId gameId : ownedGames) {
                this.ownedGames.add(gameId.id());
            }
        }
        else {
            this.ownedGames = new ArrayList<>();
        }
    }

    public static UserJpaEntity fromDomain(User user) {
        return new UserJpaEntity(
                user.getId().id(),
                user.getUsername(),
                user.getOwnedGames()
        );
    }

    public User toDomain() {
        List<GameId> ownedGames = new ArrayList<>();
        if (this.ownedGames != null) {
            for (UUID gameId : this.ownedGames) {
                ownedGames.add(new GameId(gameId));
            }
        }

        return new User(
                new UserId(this.userId),
                this.username,
                ownedGames
        );
    }
}

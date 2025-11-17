package be.kdg.ipj3.shared.infrastructure.game;

import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.game.Game;
import be.kdg.ipj3.shared.domain.game.GameDescription;
import be.kdg.ipj3.shared.domain.game.GameId;
import be.kdg.ipj3.shared.domain.game.GameName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
public class GameJpaEntity {
    @Id
    private UUID gameId;
    private String gameName;
    private BigDecimal price;
    private String currency;
    private String description;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "achievement_id")
    private List<AchievementJpaEntity> achievements;

    public GameJpaEntity(UUID gameId, String gameName, BigDecimal price, String currency, String description, List<AchievementJpaEntity> achievements) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.price = price;
        this.currency = currency;
        this.description = description;
        this.achievements = achievements;
    }

    protected GameJpaEntity() {

    }

    public static GameJpaEntity fromDomain(Game game) {
        return new GameJpaEntity(
                game.gameId().gameId(),
                game.gameName().gameName(),
                game.price().balance(),
                game.price().currency().getCurrencyCode(),
                game.description().description(),
                game.achievements().stream().map(AchievementJpaEntity::fromDomain).toList()
        );
    }

    public Game toDomain() {
        return new Game(
                new GameId(this.gameId),
                new GameName(this.gameName),
                new Money(Currency.getInstance(this.currency), this.price),
                new GameDescription(this.description),
                achievements.stream().map(AchievementJpaEntity::toDomain).toList()
        );
    }
}

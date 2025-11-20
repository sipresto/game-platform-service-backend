package be.kdg.ipj3.catalog.infrastructure.jpa;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
public class GameJpaEntity {
    @Id
    @Column(name = "game_id")
    private UUID gameId;
    private String name;
    private String description;
    private BigDecimal price;
    private String categories;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private List<AchievementJpaEntity> achievements;


    protected GameJpaEntity() {} // for JPA

    public GameJpaEntity(GameId id, String name, String description, Money price, List<Achievement> achievements, List<Category> categories) {
        this.gameId = id.id();
        this.name = name;
        this.description = description;
        this.price = price.amount();
        achievements.forEach(achievement -> this.achievements.add(AchievementJpaEntity.fromDomain(achievement)));
        this.categories = "";
        categories.forEach(category -> this.categories += category.name() + ", ");
    }

    public static GameJpaEntity fromDomain(Game game) {
        return new GameJpaEntity(
                game.getId(),
                game.getName(),
                game.getDescription(),
                game.getPrice(),
                game.getAchievements(),
                game.getCategories()
        );
    }

    public Game toDomain() {
        List<Category> categories = new ArrayList<>();
        List<String> categoriesStrings = List.of(this.categories.split(", "));
        categoriesStrings.forEach(category -> categories.add(Category.valueOf(category)));

        List<Achievement> achievements = new ArrayList<>();
        this.achievements.forEach(achievement -> achievements.add(achievement.toDomain()));

        return new Game(
                new GameId(this.gameId),
                this.name,
                this.description,
                Money.of(this.price),
                achievements,
                categories
        );
    }
}

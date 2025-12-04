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
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "games")
public class JpaGameEntity {
    @Id
    @Column(name = "game_id")
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String serviceUrl;
    private String rules;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "game_categories",
            joinColumns = @JoinColumn(name = "game_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<Category> categories;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private List<JpaAchievementEntity> achievements;


    protected JpaGameEntity() {} // for JPA

    public JpaGameEntity(GameId id, String name, String description, Money price, String serviceUrl, String rules, List<Achievement> achievements, List<Category> categories) {
        this.id = id.id();
        this.name = name;
        this.description = description;
        this.price = price.amount();
        this.serviceUrl = serviceUrl;
        this.rules = rules;
        this.achievements = new ArrayList<>();
        achievements.forEach(a -> this.achievements.add(JpaAchievementEntity.fromDomain(a)));
        this.categories = Set.copyOf(categories);
    }

    public static JpaGameEntity fromDomain(Game game) {
        return new JpaGameEntity(
                game.getId(),
                game.getName(),
                game.getDescription(),
                game.getPrice(),
                game.getServiceUrl(),
                game.getRules(),
                game.getAchievements(),
                game.getCategories()
        );
    }

    public Game toDomain() {
        List<Category> categories = new ArrayList<>(this.categories);

        List<Achievement> achievements = new ArrayList<>();
        if (this.achievements != null) {
            this.achievements.forEach(achievement -> achievements.add(achievement.toDomain()));
        }

        return new Game(
                new GameId(this.id),
                this.name,
                this.description,
                Money.of(this.price),
                this.serviceUrl,
                this.rules,
                achievements,
                categories
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getRules() {
        return rules;
    }

    public Set<Category> getCategories() { return categories; }

    public List<JpaAchievementEntity> getAchievements() {
        return achievements;
    }
}

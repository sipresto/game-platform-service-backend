package be.kdg.ipj3.catalog.infrastructure.jpa;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
public class JpaGameEntity {
    @Id
    @Column(name = "game_id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    private BigDecimal price;

    @Column(nullable = false)
    private String serviceUrl;

    @Size(max = 2000)
    private String rules;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "game_categories",
            joinColumns = @JoinColumn(name = "game_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private List<Category> categories;

    @OneToMany(
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<JpaAchievementEntity> achievements = new ArrayList<>();

    private String imageUrl;

    @Column(nullable = false)
    private Instant lastRegistration;

    protected JpaGameEntity() {
    } // for JPA


    public JpaGameEntity(UUID id, String name, String description, BigDecimal price, String serviceUrl, String rules,
                         List<Category> categories, String imageUrl, Instant lastRegistration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.serviceUrl = serviceUrl;
        this.rules = rules;
        this.categories = categories;
        this.imageUrl = imageUrl;
        this.lastRegistration = lastRegistration;
    }

    public static JpaGameEntity fromDomain(Game game) {
        JpaGameEntity jpaGame = new JpaGameEntity(
                game.getId().id(),
                game.getName(),
                game.getDescription(),
                game.getPrice().amount(),
                game.getServiceUrl(),
                game.getRules(),
                game.getCategories(),
                game.getImageUrl(),
                game.getLastRegistration()
        );

        game.getAchievements().stream()
                .map(JpaAchievementEntity::fromDomain)
                .forEach(jpaGame::addAchievement);

        return jpaGame;
    }

    public Game toDomain() {
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
                new ArrayList<>(this.categories),
                this.imageUrl,
                this.lastRegistration
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

    public List<Category> getCategories() {
        return categories;
    }

    public List<JpaAchievementEntity> getAchievements() {
        return achievements;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Instant getLastRegistration() {
        return lastRegistration;
    }

    private void addAchievement(JpaAchievementEntity achievement) {
        this.achievements.add(achievement);
        achievement.setGame(this);
    }
}

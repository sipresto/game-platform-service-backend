package be.kdg.ipj3.catalog.infrastructure.jpa;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.shared.domain.CountTrigger;
import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.CountingStrategy;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "achievements")
public class JpaAchievementEntity {
    @Id
    @Column(name = "achievement_id", nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String description;

    private Integer requiredValue;

    @Column(nullable = false)
    private int points;

    @Enumerated(EnumType.STRING)
    private CountingStrategy countingStrategy;

    @Enumerated(EnumType.STRING)
    private CountTrigger countTrigger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private JpaGameEntity game;

    public JpaAchievementEntity(UUID id, String name, String code, String description, Integer requiredValue,
                                int points, CountingStrategy countingStrategy, CountTrigger countTrigger) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.requiredValue = requiredValue;
        this.points = points;
        this.countingStrategy = countingStrategy;
        this.countTrigger = countTrigger;
    }

    protected JpaAchievementEntity() {
    } // for JPA

    public static JpaAchievementEntity fromDomain(Achievement achievement) {
        return new JpaAchievementEntity(
                achievement.getId().id(),
                achievement.getName(),
                achievement.getCode(),
                achievement.getDescription(),
                achievement.getRequiredValue(),
                achievement.getPoints(),
                achievement.getCountingStrategy(),
                achievement.getCountTrigger()
        );
    }

    public Achievement toDomain() {
        return new Achievement(
                new AchievementId(this.id),
                this.name,
                this.code,
                this.description,
                this.requiredValue,
                this.points,
                this.countingStrategy,
                this.countTrigger
        );
    }

    public void setGame(JpaGameEntity game) {
        this.game = game;
    }
}

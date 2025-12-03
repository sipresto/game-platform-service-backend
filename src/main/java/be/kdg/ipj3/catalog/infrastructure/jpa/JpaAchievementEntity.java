package be.kdg.ipj3.catalog.infrastructure.jpa;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.shared.domain.AchievementId;
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
    private String description;

    @Column(nullable = false)
    private int requiredValue;

    @Column(nullable = false)
    private int points;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private JpaGameEntity game;

    public JpaAchievementEntity(UUID id, String name, String description, int requiredValue, int points) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredValue = requiredValue;
        this.points = points;
    }

    protected JpaAchievementEntity() {
    } // for JPA

    public static JpaAchievementEntity fromDomain(Achievement achievement) {
        return new JpaAchievementEntity(
                achievement.getId().id(),
                achievement.getName(),
                achievement.getDescription(),
                achievement.getRequiredValue(),
                achievement.getPoints()
        );
    }

    public Achievement toDomain() {
        return new Achievement(
                new AchievementId(this.id),
                this.name,
                this.description,
                this.requiredValue,
                this.points
        );
    }

    public void setGame(JpaGameEntity game) {
        this.game = game;
    }
}

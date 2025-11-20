package be.kdg.ipj3.catalog.infrastructure.jpa;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.AchievementId;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "achievements")
public class AchievementJpaEntity {
    @Id
    @Column(name = "achievement_id")
    private UUID achievementId;
    private String achievementName;
    private String achievementDescription;
    private int points;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private GameJpaEntity game;

    public AchievementJpaEntity(UUID achievementId, String achievementName, String achievementDescription, int points) {
        this.achievementId = achievementId;
        this.achievementName = achievementName;
        this.achievementDescription = achievementDescription;
        this.points = points;
    }

    protected AchievementJpaEntity() {} // for JPA

    public static AchievementJpaEntity fromDomain(Achievement achievement) {
        return new AchievementJpaEntity(
                achievement.getId().id(),
                achievement.getName(),
                achievement.getDescription(),
                achievement.getPoints()
        );
    }

    public Achievement toDomain() {
        return new Achievement(
                new AchievementId(this.achievementId),
                this.achievementDescription,
                this.achievementName,
                this.points,
                this.game.toDomain().getId()
                );
    }

    public void addGame(Game game) {
        this.game = GameJpaEntity.fromDomain(game);
    }
}

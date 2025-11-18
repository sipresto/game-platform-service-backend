package be.kdg.ipj3.catalog.infrastructure;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.shared.domain.AchievementId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    public AchievementJpaEntity(UUID achievementId, String achievementName, String achievementDescription, int points) {
        this.achievementId = achievementId;
        this.achievementName = achievementName;
        this.achievementDescription = achievementDescription;
        this.points = points;
    }

    protected AchievementJpaEntity() {
    }

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
                this.points
                );
    }
}

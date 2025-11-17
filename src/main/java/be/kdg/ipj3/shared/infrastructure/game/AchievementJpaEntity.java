package be.kdg.ipj3.shared.infrastructure.game;

import be.kdg.ipj3.shared.domain.game.Achievement;
import be.kdg.ipj3.shared.domain.game.AchievementDescription;
import be.kdg.ipj3.shared.domain.game.AchievementId;
import be.kdg.ipj3.shared.domain.game.AchievementName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

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
                achievement.achievementId().achievementId(),
                achievement.achievementName().achievementName(),
                achievement.achievementDescription().description(),
                achievement.points()
        );
    }

    public Achievement toDomain() {
        return new Achievement(
                this.points,
                new AchievementDescription(this.achievementDescription),
                new AchievementName(this.achievementName),
                new AchievementId(this.achievementId)
        );
    }
}

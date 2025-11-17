package be.kdg.ipj3.shared.domain.game;

import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.UUID;

@Entity
public class Achievement {
    @Identity
    private final AchievementId achievementId;
    private final AchievementName achievementName;
    private final AchievementDescription achievementDescription;
    private int points;

    public Achievement(AchievementName achievementName, AchievementDescription achievementDescription, int points) {
        this.achievementId = new AchievementId(UUID.randomUUID());
        this.achievementName = achievementName;
        this.achievementDescription = achievementDescription;
        this.points = points;
    }

    //only for jpa
    public Achievement(int points, AchievementDescription achievementDescription, AchievementName achievementName, AchievementId achievementId) {
        this.points = points;
        this.achievementDescription = achievementDescription;
        this.achievementName = achievementName;
        this.achievementId = achievementId;
    }

    public AchievementId achievementId() {
        return achievementId;
    }

    public AchievementName achievementName() {
        return achievementName;
    }

    public AchievementDescription achievementDescription() {
        return achievementDescription;
    }

    public int points() {
        return points;
    }

    public void updatePoints(int points) {
        this.points = points;
    }
}

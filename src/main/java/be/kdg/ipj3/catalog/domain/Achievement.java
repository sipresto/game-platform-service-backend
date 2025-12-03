package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity
public class Achievement {
    @Identity
    private final AchievementId id;
    private final String name;
    private final String description;
    private final int requiredValue;
    private final int points;

    public Achievement(AchievementId id, String name, String description, int requiredValue, int points) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredValue = requiredValue;
        this.points = points;
    }

    public Achievement(String name, String description, int requiredValue, int points) {
        this(AchievementId.create(), name, description, requiredValue, points);
    }

    public AchievementId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getRequiredValue() {
        return requiredValue;
    }

    public int getPoints() {
        return points;
    }
}

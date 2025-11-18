package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
public class Achievement {
    @Identity
    private final AchievementId id;
    private final String name;
    private final String description;
    private int points;

    public Achievement(AchievementId id, String name, String description, int points) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.points = points;
    }

    public Achievement(String name, String description, int points) {
        this(AchievementId.create(), name, description, points);
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

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int points) {
        this.points -= points;
    }
}

package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.GameId;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity
public class Achievement {
    @Identity
    private final AchievementId id;
    private final String name;
    private final String description;
    private int points;
    private final GameId game;

    public Achievement(AchievementId id, String name, String description, int points, GameId game) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.points = points;
        this.game = game;
    }

    public Achievement(String name, String description, int points, GameId game) {
        this(AchievementId.create(), name, description, points, game);
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

    public GameId getGameId() {
        return game;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int points) {
        this.points -= points;
    }

}

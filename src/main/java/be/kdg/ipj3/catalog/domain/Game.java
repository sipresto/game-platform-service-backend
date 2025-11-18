package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;

@AggregateRoot
public class Game {
    @Identity
    private final GameId id;
    private final String name;
    private final String description;
    private final Money price;
    private final List<AchievementId> achievements = new ArrayList<>();

    public Game(GameId id, String name, String description, Money price, List<AchievementId> achievements) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Game(String name, String description, Money price) {
        this(GameId.create(), name, description, price, List.of());
    }

    public GameId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }

    public List<AchievementId> getAchievements() {
        return achievements;
    }

    public void addAchievement(AchievementId achievementId) {
        achievements.add(achievementId);
    }
}

package be.kdg.ipj3.catalog.domain;

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
    private final String serviceUrl;
    private final List<Category> categories;
    private final List<Achievement> achievements = new ArrayList<>();

    public Game(GameId id, String name, String description, Money price, List<Achievement> achievements, String serviceUrl, List<Category> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.serviceUrl = serviceUrl;
        this.achievements.addAll(achievements);
        this.categories = categories;
    }

    public Game(String name, String description, Money price, List<Category> categories, String serviceUrl) {
        this(GameId.create(), name, description, price, List.of(), serviceUrl, categories);
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

    public String getServiceUrl() {
        return serviceUrl;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void addAchievement(Achievement achievement) {
        achievements.add(achievement);
    }
}

package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.catalog.shared.CountTrigger;
import be.kdg.ipj3.catalog.shared.CountingStrategy;
import be.kdg.ipj3.catalog.shared.GameId;
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
    private final String rules;
    private final List<Category> categories;
    private final List<Achievement> achievements = new ArrayList<>();

    public Game(GameId id, String name, String description, Money price, String serviceUrl, String rules, List<Achievement> achievements, List<Category> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.serviceUrl = serviceUrl;
        this.rules = rules;
        this.achievements.addAll(achievements);
        this.categories = List.copyOf(categories);
    }

    public Game(String name, String description, Money price, String serviceUrl, String rules, List<Category> categories) {
        this(GameId.create(), name, description, price, serviceUrl, rules, List.of(), categories);
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

    public String getRules() {
        return rules;
    }

    public List<Achievement> getAchievements() {
        return List.copyOf(achievements);
    }

    public List<Category> getCategories() {
        return List.copyOf(categories);
    }

    public void addAchievement(String name, String code, String description, Integer requiredValue, int points,
                               CountingStrategy countingStrategy, CountTrigger countTrigger) {
        achievements.add(new Achievement(name, code, description, requiredValue, points, countingStrategy, countTrigger));
    }

    public boolean hasCategory(Category category) {
        return categories.contains(category);
    }
}

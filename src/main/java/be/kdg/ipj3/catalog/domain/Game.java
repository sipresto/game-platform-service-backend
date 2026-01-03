package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.catalog.shared.CountTrigger;
import be.kdg.ipj3.catalog.shared.CountingStrategy;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AggregateRoot
public class Game {
    @Identity
    private final GameId id;
    private final String name;
    private final String description;
    private final Money price;
    private final String backendUrl;
    private final String frontendUrl;
    private final String rules;
    private final List<Category> categories;
    private final List<Achievement> achievements = new ArrayList<>();
    private final String imageUrl;
    private Instant lastRegistration;
    private boolean isActive;
    private final int maxPlayers;

    public Game(GameId id, String name, String description, Money price, String backendUrl, String frontendUrl,
                String rules, List<Achievement> achievements, List<Category> categories, String imageUrl,
                Instant lastRegistration, boolean isActive, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.backendUrl = backendUrl;
        this.frontendUrl = frontendUrl;
        this.rules = rules;
        this.achievements.addAll(achievements);
        this.categories = List.copyOf(categories);
        this.imageUrl = imageUrl;
        this.lastRegistration = lastRegistration;
        this.isActive = isActive;
        this.maxPlayers = maxPlayers;
    }

    public Game(GameId id, String name, String description, Money price, String backendUrl, String frontendUrl,
                String rules, List<Achievement> achievements, List<Category> categories, String imageUrl,
                Instant lastRegistration, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.backendUrl = backendUrl;
        this.frontendUrl = frontendUrl;
        this.rules = rules;
        this.achievements.addAll(achievements);
        this.categories = List.copyOf(categories);
        this.imageUrl = imageUrl;
        this.lastRegistration = lastRegistration;
        this.isActive = true;
        this.maxPlayers = maxPlayers;
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

    public String getBackendUrl() {
        return backendUrl;
    }

    public String getFrontendUrl() {
        return frontendUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public Instant getLastRegistration() {
        return lastRegistration;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void addAchievement(String name, String code, String description, Integer requiredValue, int points,
                               CountingStrategy countingStrategy, CountTrigger countTrigger) {
        achievements.add(new Achievement(name, code, description, requiredValue, points, countingStrategy, countTrigger));
    }

    public void activate(Instant registrationTime) {
        this.isActive = true;
        this.lastRegistration = registrationTime;
    }

    public List<Category> getCategory() {
        return categories;
    }
}

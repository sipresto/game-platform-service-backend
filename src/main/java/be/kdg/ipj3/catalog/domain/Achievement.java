package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.CountTrigger;
import be.kdg.ipj3.shared.domain.CountingStrategy;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity
public class Achievement {
    @Identity
    private final AchievementId id;
    private final String name;
    private final String code;
    private final String description;
    private final Integer requiredValue;
    private final int points;
    private final CountingStrategy countingStrategy;
    private final CountTrigger countTrigger;

    public Achievement(AchievementId id, String name, String code, String description, Integer requiredValue,
                       int points, CountingStrategy countingStrategy, CountTrigger countTrigger) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.requiredValue = requiredValue;
        this.points = points;
        this.countingStrategy = countingStrategy;
        this.countTrigger = countTrigger;
    }

    public Achievement(String name, String code, String description, Integer requiredValue,
                       int points, CountingStrategy countingStrategy, CountTrigger countTrigger) {
        this(AchievementId.create(), name, code, description, requiredValue, points,
                countingStrategy, countTrigger);
    }

    public AchievementId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getRequiredValue() {
        return requiredValue;
    }

    public int getPoints() {
        return points;
    }

    public CountingStrategy getCountingStrategy() {
        return countingStrategy;
    }

    public CountTrigger getCountTrigger() {
        return countTrigger;
    }

    public boolean isCountedAfterGame() {
        return requiredValue != null || countingStrategy != null || countTrigger != null;
    }
}

package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.AchievementEvaluationRule;
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
    private final String description;
    private final int requiredValue;
    private final int points;
    private final CountingStrategy countingStrategy;
    private final CountTrigger countTrigger;
    private final String ruleKey;

    public Achievement(AchievementId id, String name, String description, int requiredValue, int points,
                       CountingStrategy countingStrategy, CountTrigger countTrigger, String ruleKey) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredValue = requiredValue;
        this.points = points;
        this.countingStrategy = countingStrategy;
        this.countTrigger = countTrigger;
        this.ruleKey = ruleKey;
    }

    public Achievement(String name, String description, int requiredValue, int points,
                       CountingStrategy countingStrategy, CountTrigger countTrigger, String ruleKey) {
        this(AchievementId.create(), name, description, requiredValue, points, countingStrategy, countTrigger, ruleKey);
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

    public CountingStrategy getCountingStrategy() {
        return countingStrategy;
    }

    public CountTrigger getCountTrigger() {
        return countTrigger;
    }

    public String getRuleKey() {
        return ruleKey;
    }

    public AchievementEvaluationRule getCustomRule() {
        return ruleKey == null ? null : AchievementRulesRegistry.get(ruleKey);
    }

    public boolean hasCustomRule() {
        return ruleKey != null;
    }
}

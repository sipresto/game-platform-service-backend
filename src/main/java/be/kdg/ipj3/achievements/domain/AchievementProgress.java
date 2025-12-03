package be.kdg.ipj3.achievements.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@AggregateRoot
public class AchievementProgress {
    @Identity
    private final AchievementProgressId id;
    private final int currentProgress;
    private final Instant completedAt;
    private final Map<String, Object> metadata;

    public AchievementProgress(UserId player, AchievementId achievement,
                               int currentProgress, Instant completedAt,
                               Map<String, Object> metadata) {
        this.id = new AchievementProgressId(player, achievement);
        this.currentProgress = currentProgress;
        this.completedAt = completedAt;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    public AchievementProgress(UserId player, AchievementId achievement) {
        this(player, achievement, 0, null, new HashMap<>());
    }

    public AchievementProgressId getId() {
        return id;
    }

    public UserId getPlayer() {
        return id.userId();
    }

    public AchievementId getAchievement() {
        return id.achievementId();
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public AchievementProgress incrementProgress(int amount, int requiredValue) {
        if (isCompleted()) {
            return this;
        }

        int newProgress = this.currentProgress + amount;
        Instant completedTime = (newProgress >= requiredValue && completedAt == null)
                ? Instant.now()
                : this.completedAt;

        return new AchievementProgress(id.userId(), id.achievementId(), newProgress, completedTime, metadata);
    }

    public AchievementProgress resetProgress() {
        return new AchievementProgress(id.userId(), id.achievementId());
    }

    public boolean isCompleted() {
        return completedAt != null;
    }

    public AchievementProgress incrementProgressWithMetadata(int amount, int requiredValue, Map<String, Object> metadata) {
        this.metadata.putAll(metadata);
        return incrementProgress(amount, requiredValue);
    }
}

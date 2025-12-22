package be.kdg.ipj3.achievementProgress.domain;

import be.kdg.ipj3.catalog.shared.AchievementId;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.Objects;

@AggregateRoot
public class AchievementProgress {
    @Identity
    private final AchievementProgressId id;
    private final Integer currentProgress;
    private final Instant completedAt;

    public AchievementProgress(UserId player, AchievementId achievement, Integer currentProgress, Instant completedAt) {
        Objects.requireNonNull(player, "UserId cannot be null");
        Objects.requireNonNull(achievement, "AchievementId cannot be null");

        this.id = new AchievementProgressId(player, achievement);
        this.currentProgress = currentProgress;
        this.completedAt = completedAt;
    }

    public AchievementProgress(UserId player, AchievementId achievement) {
        this(player, achievement, null, null);
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

    public Integer getCurrentProgress() {
        return currentProgress;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AchievementProgress that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public AchievementProgress incrementProgress(int amount, int requiredValue) {
        if (isCompleted()) {
            return this;
        }

        int current = (this.currentProgress != null) ? this.currentProgress : 0;
        int newProgress = current + amount;

        Instant completedTime = (newProgress >= requiredValue && completedAt == null)
                ? Instant.now()
                : this.completedAt;

        return new AchievementProgress(id.userId(), id.achievementId(), newProgress, completedTime);
    }

    public AchievementProgress resetProgress() {
        return new AchievementProgress(id.userId(), id.achievementId());
    }

    public boolean isCompleted() {
        return completedAt != null;
    }

    public AchievementProgress markCompleted(Instant completedAt) {
        if (isCompleted()) {
            return this;
        }

        return new AchievementProgress(this.id.userId(), this.id.achievementId(), null, completedAt);
    }
}

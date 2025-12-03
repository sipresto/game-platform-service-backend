package be.kdg.ipj3.achievements.domain;

import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;

@AggregateRoot
public class AchievementProgress {
    @Identity
    private final AchievementProgressId id;
    private final UserId player;
    private final AchievementId achievement;
    private final int currentProgress;
    private final Instant completedAt;

    public AchievementProgress(UserId player, AchievementId achievement, int currentProgress, Instant completedAt) {
        this.id = new AchievementProgressId(player, achievement);
        this.player = player;
        this.achievement = achievement;
        this.currentProgress = currentProgress;
        this.completedAt = completedAt;
    }

    public AchievementProgressId getId() {
        return id;
    }

    public UserId getPlayer() {
        return player;
    }

    public AchievementId getAchievement() {
        return achievement;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public AchievementProgress incrementProgress(int amount, int requiredValue) {
        if (isCompleted()) {
            return this;
        }

        int newProgress = this.currentProgress + amount;
        Instant completedTime = (newProgress >= requiredValue && completedAt == null)
                ? Instant.now()
                : this.completedAt;

        return new AchievementProgress(player, achievement, newProgress, completedTime);
    }

    public boolean isCompleted() {
        return completedAt != null;
    }
}

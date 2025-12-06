package be.kdg.ipj3.achievements.infrastructure.jpa;

import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.catalog.shared.AchievementId;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "achievement_progress")
public class JpaAchievementProgressEntity {

    @EmbeddedId
    private JpaAchievementProgressId id;

    private Integer currentProgress;

    private Instant completedAt;

    protected JpaAchievementProgressEntity() {
    } // for JPA

    public JpaAchievementProgressEntity(UUID userId, UUID achievementId, Integer currentProgress, Instant completedAt) {
        this.id = new JpaAchievementProgressId(userId, achievementId);
        this.currentProgress = currentProgress;
        this.completedAt = completedAt;
    }

    public static JpaAchievementProgressEntity fromDomain(AchievementProgress progress) {
        return new JpaAchievementProgressEntity(
                progress.getId().userId().id(),
                progress.getId().achievementId().id(),
                progress.getCurrentProgress(),
                progress.getCompletedAt()
        );
    }

    public AchievementProgress toDomain() {
        return new AchievementProgress(
                new UserId(this.id.getUserId()),
                new AchievementId(this.id.getAchievementId()),
                this.currentProgress,
                this.completedAt
        );
    }

    public UUID getUserId() {
        return id.getUserId();
    }

    public UUID getAchievementId() {
        return id.getAchievementId();
    }

    public Integer getCurrentProgress() {
        return currentProgress;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}

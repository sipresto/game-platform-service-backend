package be.kdg.ipj3.achievementProgress.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class JpaAchievementProgressId {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID achievementId;

    protected JpaAchievementProgressId() {
    } // for JPA

    public JpaAchievementProgressId(UUID userId, UUID achievementId) {
        this.userId = userId;
        this.achievementId = achievementId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getAchievementId() {
        return achievementId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaAchievementProgressId that)) return false;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(achievementId, that.achievementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, achievementId);
    }
}

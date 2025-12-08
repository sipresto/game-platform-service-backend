package be.kdg.ipj3.achievementProgress.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

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
}

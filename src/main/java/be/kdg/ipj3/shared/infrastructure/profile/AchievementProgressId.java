package be.kdg.ipj3.shared.infrastructure.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class AchievementProgressId implements Serializable {
    @Column(name = "owner_id")
    private UUID ownerId;
    @Column(name = "game_id")
    private UUID gameId;
    @Column(name = "achievement_id")
    private UUID achievementId;

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public UUID getAchievementId() {
        return achievementId;
    }

    public AchievementProgressId(UUID ownerId, UUID gameId, UUID achievementId) {
        this.ownerId = ownerId;
        this.gameId = gameId;
        this.achievementId = achievementId;
    }
}

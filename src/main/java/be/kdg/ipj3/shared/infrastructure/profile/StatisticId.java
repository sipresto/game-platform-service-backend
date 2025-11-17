package be.kdg.ipj3.shared.infrastructure.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class StatisticId implements Serializable {
    @Column(name = "profile_id")
    private UUID profileId;

    @Column(name = "game_id")
    private UUID gameId;

    public StatisticId() {

    }

    public UUID getProfileId() {
        return profileId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public StatisticId(UUID profileId, UUID gameId) {
        this.profileId = profileId;
        this.gameId = gameId;
    }
}

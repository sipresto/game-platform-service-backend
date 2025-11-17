package be.kdg.ipj3.shared.infrastructure.profile;

import be.kdg.ipj3.shared.domain.game.GameId;
import be.kdg.ipj3.shared.domain.profile.AchievementProgress;
import be.kdg.ipj3.shared.domain.user.UserId;
import be.kdg.ipj3.shared.infrastructure.game.AchievementJpaEntity;
import be.kdg.ipj3.shared.infrastructure.game.GameJpaEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "achievement_progress")
public class AchievementProgressJpaEntity {
    @EmbeddedId
    private AchievementProgressId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ownerId")
    @JoinColumn(name = "owner_id")
    private ProfileJpaEntity owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    private GameJpaEntity game;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("achievementId")
    @JoinColumn(name = "achievement_id")
    private AchievementJpaEntity achievement;

    private int amountNeeded;
    private int amountGotten;

    public AchievementProgressJpaEntity(AchievementProgressId id, ProfileJpaEntity owner, GameJpaEntity game, AchievementJpaEntity achievement, int amountNeeded, int amountGotten) {
        this.id = id;
        this.owner = owner;
        this.game = game;
        this.achievement = achievement;
        this.amountNeeded = amountNeeded;
        this.amountGotten = amountGotten;
    }

    protected AchievementProgressJpaEntity() {

    }

    public static AchievementProgressJpaEntity fromDomain(AchievementProgress progress, ProfileJpaEntity owner, GameJpaEntity game, AchievementJpaEntity achievement) {
        AchievementProgressId id = new AchievementProgressId(progress.ownerId().userId(), progress.gameId().gameId(), progress.achievement().achievementId().achievementId());
        return new AchievementProgressJpaEntity(
                id,
                owner,
                game,
                achievement,
                progress.amountNeeded(),
                progress.amountGotten()
        );
    }

    public AchievementProgress toDomain() {
        return new AchievementProgress(
                new UserId(this.id.getOwnerId()),
                new GameId(this.id.getGameId()),
                this.amountNeeded,
                this.amountGotten,
                this.achievement.toDomain()
        );
    }
}

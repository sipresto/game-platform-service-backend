package be.kdg.ipj3.shared.infrastructure.profile;

import be.kdg.ipj3.shared.domain.game.GameId;
import be.kdg.ipj3.shared.domain.profile.Statistic;
import jakarta.persistence.*;

@Entity
@Table(name = "statistics")
public class StatisticJpaEntity {
    @EmbeddedId
    private StatisticId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("profileId")
    @JoinColumn(name = "profile_id")
    private ProfileJpaEntity profile;

    private int gamesWon;
    private int gamesLost;

    public static StatisticJpaEntity fromDomain(Statistic statistic, ProfileJpaEntity profile) {
        StatisticId id = new StatisticId(profile.getUserId(), statistic.gameId().gameId());
        StatisticJpaEntity entity = new StatisticJpaEntity();
        entity.setId(id);
        entity.setProfile(profile);
        entity.setGamesWon(statistic.gamesWon());
        entity.setGamesLost(statistic.gamesLost());
        return entity;
    }

    public Statistic toDomain() {
        Statistic statistic = new Statistic(new GameId(this.id.getGameId()));
        for (int i = 0; i < this.gamesWon; i++) {
            statistic.addGameWon();
        }
        for (int i = 0; i < this.gamesLost; i++) {
            statistic.addGameLost();
        }
        return statistic;
    }

    public void setId(StatisticId id) {
        this.id = id;
    }

    public void setProfile(ProfileJpaEntity profile) {
        this.profile = profile;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }
}

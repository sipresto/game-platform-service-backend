package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
public class Statistic {
    @Identity
    private final UserId player;
    @Identity
    private final GameId game;
    private final int gamesWon;
    private final int gamesLost;

    public Statistic(UserId player, GameId game, int gamesWon, int gamesLost) {
        this.player = player;
        this.game = game;
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
    }

    public Statistic(UserId player, GameId game) {
        this(player, game, 0, 0);
    }

    public UserId getPlayer() {
        return player;
    }

    public GameId getGame() {
        return game;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
    }
}

package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
public record Statistic(@Identity UserId player, @Identity GameId game, int gamesWon, int gamesLost) {

    public Statistic(UserId player, GameId game) {
        this(player, game, 0, 0);
    }

    @Override
    public UserId player() {
        return player;
    }

    @Override
    public GameId game() {
        return game;
    }
}

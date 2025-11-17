package be.kdg.ipj3.shared.domain.profile;

import be.kdg.ipj3.shared.domain.game.GameId;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class Statistic {
    private int gamesWon;
    private int gamesLost;
    private final GameId gameId;

    public Statistic(GameId gameId) {
        gamesLost = 0;
        gamesWon = 0;
        this.gameId = gameId;
    }

    public int gamesWon() {
        return gamesWon;
    }

    public int gamesLost() {
        return gamesLost;
    }

    public GameId gameId() {
        return gameId;
    }

    public void addGameWon(){
        gamesWon++;
    }

    public void addGameLost(){
        gamesLost++;
    }

    public int calculateWinLosePercentage(){
        return (gamesWon * 100) / gamesLost;
    }
}

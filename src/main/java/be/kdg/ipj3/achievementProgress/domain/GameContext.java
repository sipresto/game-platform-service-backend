package be.kdg.ipj3.achievementProgress.domain;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.sessions.shared.SessionStatus;
import be.kdg.ipj3.users.shared.UserId;

public record GameContext(GameId game, UserId player, UserId winner, SessionStatus result, Difficulty difficulty) {

    public boolean isWinner() {
        return winner != null && winner.equals(player);
    }

    public boolean isDraw() {
        return result == SessionStatus.DRAW;
    }

    public boolean isAgainstAI() {
        return difficulty != null;
    }
}

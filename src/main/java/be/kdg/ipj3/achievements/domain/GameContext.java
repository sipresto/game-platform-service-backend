package be.kdg.ipj3.achievements.domain;

import be.kdg.ipj3.lobbies.domain.Difficulty;
import be.kdg.ipj3.sessions.domain.SessionResult;

import java.util.UUID;

public record GameContext(UUID game, UUID player, UUID winner, SessionResult result, Difficulty difficulty) {

    public boolean isWinner() {
        return winner != null && winner.equals(player);
    }

    public boolean isDraw() {
        return result == SessionResult.DRAW;
    }

    public boolean isAgainstAI() {
        return difficulty != null;
    }
}

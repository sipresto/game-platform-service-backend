package be.kdg.ipj3.achievementProgress.domain;

import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.sessions.shared.SessionResult;

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

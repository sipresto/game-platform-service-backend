package be.kdg.ipj3.achievements.domain;

import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.sessions.domain.session.SessionResult;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;

import java.util.Map;
import java.util.Optional;

public record GameContext(GameId game, UserId player, UserId winner, SessionResult result, Difficulty difficulty,
                          Map<String, Object> gameSpecificData) {

    public boolean isWinner() {
        return winner != null && winner.equals(player);
    }

    public boolean isLoser() {
        return (winner != null && !winner.equals(player)) || (winner == null && result == SessionResult.AI_WIN);
    }

    public boolean isDraw() {
        return result == SessionResult.DRAW;
    }

    public boolean isAgainstAI() {
        return difficulty != null;
    }

    public <T> Optional<T> getGameData(String key, Class<T> type) {
        Object value = gameSpecificData.get(key);
        return Optional.ofNullable(value)
                .filter(type::isInstance)
                .map(type::cast);
    }
}

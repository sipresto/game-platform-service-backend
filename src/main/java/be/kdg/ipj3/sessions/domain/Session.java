package be.kdg.ipj3.sessions.domain;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.domain.Difficulty;
import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.List;

@AggregateRoot
public class Session {
    @Identity
    private final SessionId id;
    private final GameId game;
    private final List<UserId> players;
    private final Difficulty difficulty;
    private final SessionResult result;
    private final UserId winner;
    private final Instant completedAt;

    public Session(SessionId id, GameId game, List<UserId> players, Difficulty difficulty,
                   UserId winner, SessionResult result, Instant completedAt) {
        this.id = id;
        this.game = game;
        this.players = List.copyOf(players);
        this.difficulty = difficulty;
        this.result = result;
        this.winner = winner;
        this.completedAt = completedAt;
    }

    public Session(GameId game, List<UserId> players, Difficulty difficulty, UserId winner, SessionResult result) {
        this(SessionId.create(), game, players, difficulty, winner, result, Instant.now());
    }

    public SessionId getId() {
        return id;
    }

    public GameId getGame() {
        return game;
    }

    public List<UserId> getPlayers() {
        return players;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public UserId getWinner() {
        return winner;
    }

    public SessionResult getResult() {
        return result;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}

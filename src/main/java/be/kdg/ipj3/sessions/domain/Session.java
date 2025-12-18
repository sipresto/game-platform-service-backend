package be.kdg.ipj3.sessions.domain;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.sessions.shared.SessionStatus;
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
    private SessionStatus status;
    private UserId winner;
    private Instant completedAt;

    public Session(SessionId id, GameId game, List<UserId> players, Difficulty difficulty, SessionStatus status,
                   UserId winner, Instant completedAt) {
        this.id = id;
        this.game = game;
        this.players = List.copyOf(players);
        this.difficulty = difficulty;
        this.status = status;
        this.winner = winner;
        this.completedAt = completedAt;
    }

    public Session(GameId game, List<UserId> players, Difficulty difficulty, SessionStatus status, UserId winner) {
        this(SessionId.create(), game, players, difficulty, status, winner, Instant.now());
    }

    public Session(SessionId id, GameId game, List<UserId> players, Difficulty difficulty) {
        this(id, game, players, difficulty, SessionStatus.IN_PROGRESS, null, Instant.now());
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

    public SessionStatus getStatus() {
        return status;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void end(SessionStatus status, UserId winnerId, Instant completedAt) {
        if (this.status != SessionStatus.IN_PROGRESS) {
            return;
        }

        if (!players.contains(winnerId)) {
            this.winner = null;
        }
        else {
            this.winner = winnerId;
        }

        this.status = status;
        this.completedAt = completedAt;
    }
}

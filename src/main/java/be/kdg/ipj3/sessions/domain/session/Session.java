package be.kdg.ipj3.sessions.domain.session;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.Set;

@AggregateRoot
public class Session {
    @Identity
    private final SessionId id;
    private SessionState state;
    private final Instant createdAt;
    private final GameId game;
    private final Set<UserId> players;

    public Session(SessionId id, SessionState state, Instant createdAt, GameId game, Set<UserId> players) {
        this.id = id;
        this.state = state;
        this.createdAt = createdAt;
        this.game = game;
        this.players = players;
    }

    public Session(GameId game, Set<UserId> users) {
        this(SessionId.create(), SessionState.WAITING, Instant.now(), game, users);
    }

    public SessionId getId() {
        return id;
    }

    public SessionState getState() {
        return state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public GameId getGame() {
        return game;
    }

    public Set<UserId> getPlayers() {
        return Set.copyOf(players);
    }

    public void updateState(SessionState state) {
        this.state = state;
    }

    public void addPlayer(UserId player) {
        this.players.add(player);
    }

    public void removePlayer(UserId player) {
        this.players.remove(player);
    }
}

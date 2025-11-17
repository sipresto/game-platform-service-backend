package be.kdg.ipj3.session.domain.session;

import be.kdg.ipj3.shared.domain.game.GameId;
import be.kdg.ipj3.shared.domain.user.UserId;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
public class Session {
    @Identity
    private final SessionId sessionId;
    private SessionState state;
    private final Instant createdAt;
    private final List<UserId> userIds;
    private final GameId gameId;

    public Session(GameId gameId, List<UserId> userIds) {
        this.sessionId = new SessionId(UUID.randomUUID());
        this.createdAt = Instant.now();
        this.state = SessionState.WAITING;
        this.userIds = userIds;
        this.gameId = gameId;
    }

    public SessionId sessionId() {
        return sessionId;
    }

    public SessionState state() {
        return state;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public List<UserId> userIds() {
        return userIds;
    }

    public GameId gameId() {
        return gameId;
    }

    public void updateState(SessionState state) {
        this.state = state;
    }

    public void removeUser(UserId userId) {
        this.userIds.remove(userId);
    }

    public void addUserId(UserId userId) {
        this.userIds.add(userId);
    }
}

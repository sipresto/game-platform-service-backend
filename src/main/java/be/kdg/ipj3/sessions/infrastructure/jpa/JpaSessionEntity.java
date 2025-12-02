package be.kdg.ipj3.sessions.infrastructure.jpa;

import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.sessions.domain.session.Session;
import be.kdg.ipj3.sessions.domain.session.SessionId;
import be.kdg.ipj3.sessions.domain.session.SessionResult;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sessions")
public class JpaSessionEntity {
    @Id
    @Column(name = "session_id")
    private UUID id;

    @Column(nullable = false)
    private UUID gameId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "session_players",
            joinColumns = @JoinColumn(name = "session_id")
    )
    @Column(name = "player_id")
    @OrderColumn(name = "player_order")
    private List<UUID> players = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private UUID winnerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionResult result;

    @Column(nullable = false)
    private Instant completedAt;

    protected JpaSessionEntity() {
    } // for JPA

    public JpaSessionEntity(UUID id, UUID gameId, List<UUID> players, Difficulty difficulty,
                            UUID winnerId, SessionResult result, Instant completedAt) {
        this.id = id;
        this.gameId = gameId;
        this.players = new ArrayList<>(players);
        this.difficulty = difficulty;
        this.winnerId = winnerId;
        this.result = result;
        this.completedAt = completedAt;
    }

    public static JpaSessionEntity fromDomain(Session session) {
        return new JpaSessionEntity(
                session.getId().id(),
                session.getGame().id(),
                session.getPlayers().stream().map(UserId::id).toList(),
                session.getDifficulty(),
                session.getWinner() != null ? session.getWinner().id() : null,
                session.getResult(),
                session.getCompletedAt()
        );
    }

    public Session toDomain() {
        return new Session(
                new SessionId(this.id),
                new GameId(this.gameId),
                this.players.stream().map(UserId::new).toList(),
                this.difficulty,
                this.winnerId != null ? new UserId(this.winnerId) : null,
                this.result,
                this.completedAt
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getGameId() {
        return gameId;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public SessionResult getResult() {
        return result;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}

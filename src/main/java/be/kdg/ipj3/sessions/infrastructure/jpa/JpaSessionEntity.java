package be.kdg.ipj3.sessions.infrastructure.jpa;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.sessions.shared.SessionStatus;
import be.kdg.ipj3.users.shared.UserId;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    private UUID winnerId;

    @Column(nullable = false)
    private Instant completedAt;

    protected JpaSessionEntity() {
    } // for JPA

    public JpaSessionEntity(UUID id, UUID gameId, List<UUID> players, Difficulty difficulty,
                            SessionStatus status, UUID winnerId, Instant completedAt) {
        this.id = id;
        this.gameId = gameId;
        this.players = new ArrayList<>(players);
        this.difficulty = difficulty;
        this.status = status;
        this.winnerId = winnerId;
        this.completedAt = completedAt;
    }

    public static JpaSessionEntity fromDomain(Session session) {
        return new JpaSessionEntity(
                session.getId().id(),
                session.getGame().id(),
                session.getPlayers().stream().map(UserId::id).toList(),
                session.getDifficulty(),
                session.getStatus(),
                session.getWinner() != null ? session.getWinner().id() : null,
                session.getCompletedAt()
        );
    }

    public Session toDomain() {
        return new Session(
                new SessionId(this.id),
                new GameId(this.gameId),
                this.players.stream().map(UserId::new).toList(),
                this.difficulty,
                this.status,
                this.winnerId != null ? new UserId(this.winnerId) : null,
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

    public SessionStatus getStatus() {
        return status;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}

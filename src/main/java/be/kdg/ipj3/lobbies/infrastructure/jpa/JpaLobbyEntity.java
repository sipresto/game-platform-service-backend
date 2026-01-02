package be.kdg.ipj3.lobbies.infrastructure.jpa;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyId;
import be.kdg.ipj3.lobbies.domain.LobbyStatus;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "lobbies")
public class JpaLobbyEntity {
    @Id
    @Column(name = "lobby_id")
    private UUID id;

    private String password;

    @Column(nullable = false)
    private int maxPlayers;

    @Column(nullable = false)
    private UUID gameId;

    @Column(nullable = false)
    private UUID hostId;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "lobby_players",
            joinColumns = @JoinColumn(name = "lobby_id")
    )
    @Column(name = "player_id")
    private List<UUID> players;

    @Column(nullable = false)
    private String status;

    private String activeGameUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "bots",
            joinColumns = @JoinColumn(name = "lobby_id")
    )
    @Column(name = "bot")
    private List<Difficulty> bots;


    protected JpaLobbyEntity() {
    } //for JPA

    public JpaLobbyEntity(UUID id, String password, int maxPlayers, UUID gameId, UUID hostId, List<UUID> players, String status, String activeGameUrl, Instant lastActivityAt, List<Difficulty> bots) {
        this.id = id;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.gameId = gameId;
        this.hostId = hostId;
        this.players = players;
        this.status = status;
        this.activeGameUrl = activeGameUrl;
        this.lastActivityAt = lastActivityAt;
        this.bots = bots;
    }

    public static JpaLobbyEntity fromDomain(Lobby lobby) {
        return new JpaLobbyEntity(
                lobby.getId().id(),
                lobby.getPassword(),
                lobby.getMaxPlayers(),
                lobby.getGame().id(),
                lobby.getHost().id(),
                lobby.getPlayers().stream()
                        .map(UserId::id)
                        .collect(Collectors.toList()),
                lobby.getStatus().name(),
                lobby.getActiveGameUrl(),
                lobby.getLastActivityAt(),
                lobby.getBots()
        );
    }

    public Lobby toDomain() {
        return new Lobby(
                new LobbyId(this.id),
                this.password,
                this.maxPlayers,
                new GameId(this.gameId),
                new UserId(this.hostId),
                this.players.stream()
                        .map(UserId::new)
                        .collect(Collectors.toList()),
                LobbyStatus.valueOf(this.status),
                this.activeGameUrl,
                this.lastActivityAt,
                this.bots
        );
    }

    public UUID getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public UUID getGameId() {
        return gameId;
    }

    public UUID getHostId() {
        return hostId;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public String getStatus() {
        return status;
    }

    public String getActiveGameUrl() {
        return activeGameUrl;
    }

}
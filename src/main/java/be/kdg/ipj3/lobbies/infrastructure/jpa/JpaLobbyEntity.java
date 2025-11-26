package be.kdg.ipj3.lobbies.infrastructure.jpa;

import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyStatus;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import jakarta.persistence.*;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Entity
@Table(name = "lobbies")
public class JpaLobbyEntity {
    @Id
    @Column(name = "lobby_id")
    private UUID id;

    private String password;

    @Column(name = "max_players", nullable = false)
    private int maxPlayers;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Column(name = "host_id", nullable = false)
    private UUID hostId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lobby_players", joinColumns = @JoinColumn(name = "lobby_id"))
    @Column(name = "player_id")
    private Set<UUID> players;

    @Column(nullable = false)
    private String status;

    @Column(name = "active_game_url")
    private String activeGameUrl;


    protected JpaLobbyEntity() {} //for JPA

    public JpaLobbyEntity(UUID id, String password, int maxPlayers, UUID gameId, UUID hostId, Set<UUID> players, String status, String activeGameUrl) {
        this.id = id;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.gameId = gameId;
        this.hostId = hostId;
        this.players = players;
        this.status = status;
        this.activeGameUrl = activeGameUrl;
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
                        .collect(Collectors.toSet()),
                lobby.getStatus().name(),
                lobby.getActiveGameUrl()
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
                        .collect(Collectors.toSet()),
                LobbyStatus.valueOf(this.status),
                this.activeGameUrl
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

    public Set<UUID> getPlayers() {
        return players;
    }

    public String getStatus() {
        return status;
    }

    public String getActiveGameUrl() {
        return activeGameUrl;
    }

}
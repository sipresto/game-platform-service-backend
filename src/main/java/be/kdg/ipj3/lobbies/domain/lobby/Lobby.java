package be.kdg.ipj3.lobbies.domain.lobby;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AggregateRoot
public class Lobby {
    @Identity
    private final LobbyId id;
    private final int maxPlayers;
    private final GameId game;
    private final String password;
    private UserId host;
    private final List<UserId> players = new ArrayList<>();
    private LobbyStatus status;
    private String activeGameUrl;

    public Lobby(LobbyId id, String password, int maxPlayers, GameId game, UserId host, List<UserId> players, LobbyStatus status, String activeGameUrl) {
        this.id = id;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.game = game;
        this.host = host;
        this.players.addAll(players);
        this.status = status;
        this.activeGameUrl = activeGameUrl;
    }

    public Lobby(String password, int maxPlayers, GameId game, UserId host) {
        this(LobbyId.create(), password, maxPlayers, game, host, List.of(host), LobbyStatus.OPEN, null);
    }

    public void join(UserId player, String providedPassword) {
        if (status != LobbyStatus.OPEN) {
            throw new IllegalStateException("Lobby is not open for joining.");
        }
        if (players.size() >= maxPlayers) {
            throw new IllegalStateException("Lobby is full.");
        }
        if (players.contains(player)) {
            return;
        }

        if (isPrivate() && !this.password.equals(providedPassword)) {
            throw new IllegalArgumentException("Invalid lobby password.");
        }

        players.add(player);
    }

    public void leave(UserId player) {
        players.remove(player);

        if (players.isEmpty()) {
            this.status = LobbyStatus.CLOSED;
        } else if (host.equals(player)) {
            host = players.getFirst();
        }
    }

    public void validateReadyToStart(UserId requestor) {
        if (!this.host.equals(requestor)) {
            throw new IllegalStateException("Only the host can start the game.");
        }
        if (status != LobbyStatus.OPEN) {
            throw new IllegalStateException("Game has already started or is closed.");
        }
        if (players.isEmpty()) {
            throw new IllegalStateException("Cannot start an empty lobby.");
        }
    }

    public void markAsInGame(String gameUrl) {
        this.status = LobbyStatus.IN_GAME;
        this.activeGameUrl = gameUrl;
    }

    public boolean isPrivate() {
        return password != null && !password.isBlank();
    }

    public LobbyId getId() {
        return id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public GameId getGame() {
        return game;
    }

    public Set<UserId> getPlayers() {
        return Set.copyOf(players);
    }

    public LobbyStatus getStatus() { return status; }
    public String getActiveGameUrl() { return activeGameUrl; }
    public String getPassword() { return password; }
    public UserId getHost() { return host; }
}

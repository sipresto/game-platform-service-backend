package be.kdg.ipj3.lobbies.domain.lobby;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.HashSet;
import java.util.Set;

@AggregateRoot
public class Lobby {
    @Identity
    private final LobbyId id;
    private final int maxPlayers;
    private final GameId game;
    private final Set<UserId> players = new HashSet<>();

    public Lobby(LobbyId id, int maxPlayers, GameId game, Set<UserId> players) {
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.game = game;
        this.players.addAll(players);
    }

    public Lobby(int maxPlayers, GameId game, Set<UserId> players) {
        this(LobbyId.create(), maxPlayers, game, players);
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

    public void addPlayer(UserId player) {
        players.add(player);
    }

    public void removePlayer(UserId player) {
        players.remove(player);
    }
}

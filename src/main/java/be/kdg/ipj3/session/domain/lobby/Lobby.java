package be.kdg.ipj3.session.domain.lobby;

import be.kdg.ipj3.shared.domain.game.GameId;
import be.kdg.ipj3.shared.domain.user.UserId;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.UUID;

@Entity
public class Lobby {
    @Identity
    private final LobbyId id;
    private final MaxPlayers maxPlayers;
    private final GameId gameId;
    private final UserId[] userIds;

    public Lobby(MaxPlayers maxPlayers, GameId gameId, UserId[] userIds) {
        this.maxPlayers = maxPlayers;
        this.gameId = gameId;
        this.userIds = new UserId[userIds.length];
        this.id = new LobbyId(UUID.randomUUID());
    }

    public LobbyId id() {
        return id;
    }

    public MaxPlayers maxPlayers() {
        return maxPlayers;
    }

    public GameId gameId() {
        return gameId;
    }

    public UserId[] userIds() {
        return userIds;
    }

    public void addUserId(UserId userId) {
        for(int i = 0; i < maxPlayers().maxPlayers(); i++){
            if(userIds[i] == null){
                userIds[i] = userId;
                break;
            }
        }
    }

    public void removeUserId(UserId userId) {
        for(int i = 0; i < maxPlayers().maxPlayers(); i++){
            if(userIds[i] == userId){
                userIds[i] = null;
                break;
            }
        }
    }
}

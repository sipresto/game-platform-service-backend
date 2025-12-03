package be.kdg.ipj3.lobbies.domain.lobby;

import be.kdg.ipj3.shared.domain.GameId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILobbyRepository {

    void save(Lobby lobby);

    Optional<Lobby> findById(LobbyId id);

    List<Lobby> findByStatus(LobbyStatus status);

    List<Lobby> findByStatusAndGameId(LobbyStatus status, GameId gameId);

    List<Lobby> findAll();

    void deleteAll();
}
package be.kdg.ipj3.lobbies.domain.lobby;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ILobbyRepository {
    void save(Lobby lobby);
    Optional<Lobby> findById(LobbyId id);
    List<Lobby> findByStatus(LobbyStatus status);
    List<Lobby> findByStatusAndGameId(LobbyStatus status, UUID gameId);
}
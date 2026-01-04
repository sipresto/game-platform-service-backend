package be.kdg.ipj3.lobbies.domain;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LobbyRepository {

    void save(Lobby lobby);

    Optional<Lobby> findById(LobbyId id);

    List<Lobby> findByStatus(LobbyStatus status);

    List<Lobby> findByStatusAndGameId(LobbyStatus status, GameId gameId);

    List<Lobby> findAll();

    void deleteLobbyByGameId(UUID gameId);

    void deleteAll();

    void deleteById(LobbyId lobbyId);

    List<Lobby> findByLastActivityBeforeInstant(Instant cutoff);

    Optional<Lobby> findByGameId(UUID gameId);

    Optional<Lobby> findCurrentLobbyByUserId(UserId userId);
}
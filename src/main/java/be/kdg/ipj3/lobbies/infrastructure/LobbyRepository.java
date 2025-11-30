package be.kdg.ipj3.lobbies.infrastructure;

import be.kdg.ipj3.lobbies.domain.lobby.ILobbyRepository;
import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyStatus;
import be.kdg.ipj3.lobbies.infrastructure.jpa.JpaLobbyEntity;
import be.kdg.ipj3.lobbies.infrastructure.jpa.JpaLobbyRepository;
import be.kdg.ipj3.shared.domain.GameId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LobbyRepository implements ILobbyRepository {
    private final JpaLobbyRepository jpaLobbyRepository;

    public LobbyRepository(JpaLobbyRepository jpaLobbyRepository) {
        this.jpaLobbyRepository = jpaLobbyRepository;
    }

    @Override
    public void save(Lobby lobby) {
        JpaLobbyEntity entity = JpaLobbyEntity.fromDomain(lobby);
        jpaLobbyRepository.save(entity);
    }

    @Override
    public Optional<Lobby> findById(LobbyId id) {
        return jpaLobbyRepository.findById(id.id())
                .map(JpaLobbyEntity::toDomain);
    }

    @Override
    public List<Lobby> findByStatus(LobbyStatus status) {
        return jpaLobbyRepository.findByStatus(status.name())
                .stream()
                .map(JpaLobbyEntity::toDomain)
                .toList();
    }

    @Override
    public List<Lobby> findByStatusAndGameId(LobbyStatus status, GameId gameId) {
        return jpaLobbyRepository.findByStatusAndGameId(status.name(), gameId.id())
                .stream()
                .map(JpaLobbyEntity::toDomain)
                .toList();
    }
}
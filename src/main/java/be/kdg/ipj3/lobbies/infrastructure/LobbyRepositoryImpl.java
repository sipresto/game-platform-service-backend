package be.kdg.ipj3.lobbies.infrastructure;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.shared.LobbyId;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.lobbies.domain.LobbyStatus;
import be.kdg.ipj3.lobbies.infrastructure.jpa.JpaLobbyEntity;
import be.kdg.ipj3.lobbies.infrastructure.jpa.JpaLobbyRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LobbyRepositoryImpl implements LobbyRepository {
    private final JpaLobbyRepository jpaLobbyRepository;

    public LobbyRepositoryImpl(JpaLobbyRepository jpaLobbyRepository) {
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

    @Override
    public List<Lobby> findAll() {
        return jpaLobbyRepository.findAll().stream().map(JpaLobbyEntity::toDomain).toList();
    }

    @Override
    public void deleteLobbyByGameId(UUID gameId) {
        jpaLobbyRepository.deleteByActiveGameUrlContaining(gameId.toString());
    }

    @Override
    public void deleteAll() {
        jpaLobbyRepository.deleteAll();
    }

    @Override
    public void deleteById(LobbyId lobbyId) {
        jpaLobbyRepository.deleteById(lobbyId.id());
    }

    @Override
    public List<Lobby> findByLastActivityBeforeInstant(Instant cutoff) {
        return jpaLobbyRepository.findJpaLobbyEntitiesByLastActivityAtBefore(cutoff).stream().map(JpaLobbyEntity::toDomain).toList();
    }

    @Override
    public Optional<Lobby> findByGameId(UUID activeGameId) {
        return jpaLobbyRepository.findByGameId(activeGameId.toString()).stream().map(JpaLobbyEntity::toDomain).findFirst();
    }
}
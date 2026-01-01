package be.kdg.ipj3.lobbies.infrastructure.jpa;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JpaLobbyRepository extends JpaRepository<JpaLobbyEntity, UUID> {
    List<JpaLobbyEntity> findByStatus(String status);

    @Query("SELECT l FROM JpaLobbyEntity l WHERE l.status = :status AND l.gameId = :gameId")
    List<JpaLobbyEntity> findByStatusAndGameId(@Param("status") String status, @Param("gameId") UUID gameId);

    @Modifying
    @Query("DELETE FROM JpaLobbyEntity l WHERE l.activeGameUrl IS NOT NULL AND l.activeGameUrl LIKE %:gameId%")
    void deleteByActiveGameUrlContaining(@Param("gameId") String gameId);

    List<JpaLobbyEntity> findJpaLobbyEntitiesByLastActivityAtBefore(Instant cutoff);

    @Query("SELECT l FROM JpaLobbyEntity l WHERE l.activeGameUrl IS NOT NULL AND l.activeGameUrl LIKE %:gameId%")
    List<JpaLobbyEntity> findByGameId(@Param("gameId") String gameId);

    List<JpaLobbyEntity> deleteJpaLobbyEntitiesByActiveGameUrlContaining(String activeGameUrl);

}
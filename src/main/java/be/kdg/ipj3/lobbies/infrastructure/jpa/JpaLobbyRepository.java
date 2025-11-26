package be.kdg.ipj3.lobbies.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaLobbyRepository extends JpaRepository<JpaLobbyEntity, UUID> {
    List<JpaLobbyEntity> findByStatus(String status);

    @Query("SELECT l FROM JpaLobbyEntity l WHERE l.status = :status AND l.gameId = :gameId")
    List<JpaLobbyEntity> findByStatusAndGameId(@Param("status") String status, @Param("gameId") UUID gameId);
}
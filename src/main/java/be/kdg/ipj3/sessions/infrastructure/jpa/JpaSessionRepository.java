package be.kdg.ipj3.sessions.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaSessionRepository extends JpaRepository<JpaSessionEntity, UUID> {
    @Query("SELECT s FROM JpaSessionEntity s WHERE :userId MEMBER OF s.players")
    List<JpaSessionEntity> findSessionsByUserId(UUID userId);
}

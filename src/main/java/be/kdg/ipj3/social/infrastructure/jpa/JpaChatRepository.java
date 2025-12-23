package be.kdg.ipj3.social.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface JpaChatRepository extends JpaRepository<JpaChatEntity, UUID> {
    List<JpaChatEntity> findByParticipantIdsContainingAndLastMessageAtIsNotNullOrderByLastMessageAtDesc(UUID userId);

    @Query("SELECT c FROM JpaChatEntity c LEFT JOIN FETCH c.messages WHERE c.id = :id")
    Optional<JpaChatEntity> findByIdWithMessages(UUID id);

    @Query("""
            SELECT c FROM JpaChatEntity c
            JOIN c.participantIds p
            GROUP BY c
            HAVING COUNT(p) = :size
               AND SUM(CASE WHEN p IN :participantIds THEN 1 ELSE 0 END) = :size
            """)
    Optional<JpaChatEntity> findByExactParticipants(Set<UUID> participantIds, long size);
}

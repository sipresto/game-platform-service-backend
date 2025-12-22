package be.kdg.ipj3.social.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface JpaChatRepository extends JpaRepository<JpaChatEntity, UUID> {
    List<JpaChatEntity> findByParticipantIdsContainingAndLastMessageAtIsNotNullOrderByLastMessageAtDesc(UUID userId);

    @Query("""
                SELECT c
                FROM JpaChatEntity c
                WHERE :size = (SELECT COUNT(p) FROM c.participantIds p)
                  AND c.participantIds IN :participantIds
            """)
    Optional<JpaChatEntity> findByExactParticipants(Set<UUID> participantIds, int size);
}

package be.kdg.ipj3.social.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaChatRepository extends JpaRepository<JpaChatEntity, UUID> {
    List<JpaChatEntity> findByParticipantIdsContaining(UUID userId);
}

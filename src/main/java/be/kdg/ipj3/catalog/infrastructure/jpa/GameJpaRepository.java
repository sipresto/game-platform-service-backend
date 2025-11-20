package be.kdg.ipj3.catalog.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameJpaRepository extends JpaRepository<GameJpaEntity, UUID> {
}

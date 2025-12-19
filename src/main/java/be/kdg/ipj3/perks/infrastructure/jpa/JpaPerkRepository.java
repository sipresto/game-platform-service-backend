package be.kdg.ipj3.perks.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaPerkRepository extends JpaRepository<JpaPerkEntity, UUID> {
}

package be.kdg.ipj3.shop.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaPurchaseRepository extends JpaRepository<JpaPurchaseEntity, UUID> {
}
package be.kdg.ipj3.users.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProfileRepository extends JpaRepository<JpaProfileEntity, String> {
}
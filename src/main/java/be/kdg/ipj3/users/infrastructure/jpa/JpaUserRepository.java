package be.kdg.ipj3.users.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, String> {
}
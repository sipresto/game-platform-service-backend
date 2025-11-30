package be.kdg.ipj3.users.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

    Optional<JpaUserEntity> findByKeycloakId(String keycloakId);

    @Query("SELECT g FROM JpaUserEntity u JOIN u.ownedGames g WHERE u.keycloakId = :keycloakId")
    List<UUID> findOwnedGamesByKeycloakId(String keycloakId);
}

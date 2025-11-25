package be.kdg.ipj3.users.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {
    @Query("""
    SELECT u
    FROM JpaUserEntity u
    JOIN FETCH u.ownedGames
    WHERE u.id = :id
""")
    Optional<JpaUserEntity> findByIdWithGames(UUID id);

    Optional<JpaUserEntity> findByKeycloakId(String keycloakId);
}

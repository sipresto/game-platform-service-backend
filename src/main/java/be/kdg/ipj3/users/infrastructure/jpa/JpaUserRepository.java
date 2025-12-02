package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.users.domain.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

    Optional<JpaUserEntity> findByKeycloakId(String keycloakId);

    @Query("SELECT g FROM JpaUserEntity u JOIN u.ownedGames g WHERE u.keycloakId = :keycloakId")
    List<UUID> findOwnedGamesByKeycloakId(String keycloakId);

    List<JpaUserEntity> findAllByVisibility(Visibility visibility);

    @Query("""
       SELECT ue\s
       FROM JpaUserEntity ue\s
       WHERE ue.id IN (
           SELECT f FROM JpaUserEntity u JOIN u.friends f WHERE u.id = :userId
       )
   \s""")
    List<JpaUserEntity> findFriends(@Param("userId") UUID userId);
}

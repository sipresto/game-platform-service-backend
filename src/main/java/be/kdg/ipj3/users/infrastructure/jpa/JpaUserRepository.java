package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.users.domain.Visibility;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM JpaUserEntity u WHERE u.id = :id")
    Optional<JpaUserEntity> findByIdForUpdate(@Param("id") UUID id);

    Optional<JpaUserEntity> findByKeycloakId(String keycloakId);

    @Query("""
            SELECT g
            FROM JpaUserEntity u
            JOIN u.ownedGames g
            WHERE u.id = :id
            """
    )
    List<UUID> findOwnedGamesById(UUID id);

    @Query("""
            SELECT g
            FROM JpaUserEntity u
            JOIN u.favoriteGames g
            WHERE u.id = :id
            """
    )
    List<UUID> findFavoriteGamesByUserId(UUID id);
    List<JpaUserEntity> findAllByVisibility(Visibility visibility);

    @Query("""
            SELECT ue
            FROM JpaUserEntity ue
            WHERE ue.id IN (
                SELECT f
                FROM JpaUserEntity u
                JOIN u.friends f
                WHERE u.id = :userId
            )
            """
    )
    List<JpaUserEntity> findFriends(@Param("userId") UUID userId);
}

package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IUserJpaRepository extends JpaRepository<JpaUserEntity, String> {
    @Query("""
    SELECT u
    FROM JpaUserEntity u
    JOIN FETCH u.ownedGames
    WHERE u.userId = :userId
""")
    Optional<JpaUserEntity> findByIdWithGames(String userId);
}

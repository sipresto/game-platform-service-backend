package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IUserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    @Query("""
    SELECT u
    FROM UserJpaEntity u
    JOIN FETCH u.ownedGames
    WHERE u.userId = :userId
""")
    Optional<UserJpaEntity> findByIdWithGames(String userId);
}

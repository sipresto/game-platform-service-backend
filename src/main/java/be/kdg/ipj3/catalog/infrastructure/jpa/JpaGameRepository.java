package be.kdg.ipj3.catalog.infrastructure.jpa;

import be.kdg.ipj3.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaGameRepository extends JpaRepository<JpaGameEntity, UUID> {

    @Query("""
                SELECT DISTINCT g
                FROM JpaGameEntity g
                LEFT JOIN g.categories c
                WHERE (:minPrice IS NULL OR g.price >= :minPrice)
                  AND (:maxPrice IS NULL OR g.price <= :maxPrice)
                  AND (:categories IS NULL OR :categories IS EMPTY OR c IN :categories)
            """)
    List<JpaGameEntity> filterGames(BigDecimal minPrice, BigDecimal maxPrice,
                                    List<Category> categories);

    @Query("SELECT g FROM JpaGameEntity g WHERE g.id IN :ids")
    List<JpaGameEntity> findAllByIds(List<UUID> ids);

    @Query("SELECT g.backendUrl FROM JpaGameEntity g WHERE g.id = :id")
    Optional<String> findBackendUrlById(UUID id);

    @Query("SELECT g.frontendUrl FROM JpaGameEntity g WHERE g.id = :id")
    Optional<String> findFrontendUrlById(UUID id);

    @Query("SELECT a FROM JpaAchievementEntity a WHERE a.game.id = :id")
    List<JpaAchievementEntity> findAchievementsByGameId(UUID id);

    @Query("SELECT a FROM JpaAchievementEntity a WHERE a.game.id = :gameId AND a.code = :code")
    Optional<JpaAchievementEntity> findAchievementByGameIdAndCode(UUID gameId, String code);

    @Query("""
                 SELECT g
                 FROM JpaGameEntity g
                 JOIN FETCH g.achievements
                 WHERE g.id = :id
            """)
    Optional<JpaGameEntity> findByIdWithAchievements(UUID id);
}

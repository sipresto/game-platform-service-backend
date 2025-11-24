package be.kdg.ipj3.catalog.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface JpaGameRepository extends JpaRepository<GameJpaEntity, UUID> {

    @Query("SELECT g FROM GameJpaEntity g WHERE " +
            "(:minPrice IS NULL OR g.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR g.price <= :maxPrice) AND " +
            "(:category IS NULL OR :category = '' OR " +
            "LOWER(g.categories) LIKE LOWER(CONCAT('%', :category, '%')))")
    List<GameJpaEntity> filterGames(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("category") String category);
}
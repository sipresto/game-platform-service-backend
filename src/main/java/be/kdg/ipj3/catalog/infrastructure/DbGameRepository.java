package be.kdg.ipj3.catalog.infrastructure;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.infrastructure.jpa.GameJpaEntity;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameRepository;
import be.kdg.ipj3.shared.domain.Money;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class DbGameRepository implements GameRepository {
    private final JpaGameRepository jpaGameRepository;

    public DbGameRepository(JpaGameRepository jpaGameRepository) {
        this.jpaGameRepository = jpaGameRepository;
    }

    @Override
    public void save(Game game) {
        jpaGameRepository.save(GameJpaEntity.fromDomain(game));
    }

    @Override
    public List<Game> getAllGames() {
        return jpaGameRepository.findAll()
                .stream().map(GameJpaEntity::toDomain).toList();
    }

    @Override
    public List<Game> filterGames(Money minPrice, Money maxPrice, List<Category> categories) {
        BigDecimal minPriceAmount = minPrice != null ? minPrice.amount() : null;
        BigDecimal maxPriceAmount = maxPrice != null ? maxPrice.amount() : null;

        if (categories == null || categories.isEmpty()) {
            return jpaGameRepository.filterGames(minPriceAmount, maxPriceAmount, null)
                    .stream()
                    .map(GameJpaEntity::toDomain)
                    .toList();
        }

        return categories.stream()
                .flatMap(category ->
                        jpaGameRepository.filterGames(minPriceAmount, maxPriceAmount, category.name())
                                .stream()
                                .map(GameJpaEntity::toDomain)
                )
                .distinct()
                .toList();
    }
}
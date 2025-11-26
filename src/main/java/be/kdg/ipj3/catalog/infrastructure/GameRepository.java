package be.kdg.ipj3.catalog.infrastructure;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameEntity;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GameRepository implements IGameRepository {
    private final JpaGameRepository jpaGameRepository;

    public GameRepository(JpaGameRepository jpaGameRepository) {
        this.jpaGameRepository = jpaGameRepository;
    }

    @Override
    public void save(Game game) {
        jpaGameRepository.save(JpaGameEntity.fromDomain(game));
    }

    @Override
    public Optional<Game> findById(GameId gameId) {
        return jpaGameRepository.findById(gameId.id()).map(JpaGameEntity::toDomain);
    }

    @Override
    public List<Game> findAllGames() {
        return jpaGameRepository.findAll()
                .stream().map(JpaGameEntity::toDomain).toList();
    }

    @Override
    public Optional<String> findServiceUrlByGameId(GameId id) {
        return jpaGameRepository.findServiceUrlById(id.id());
    }

    @Override
    public List<Game> filterGames(Money minPrice, Money maxPrice, List<Category> categories) {
        BigDecimal minPriceAmount = minPrice != null ? minPrice.amount() : null;
        BigDecimal maxPriceAmount = maxPrice != null ? maxPrice.amount() : null;

        if (categories == null || categories.isEmpty()) {
            return jpaGameRepository.filterGames(minPriceAmount, maxPriceAmount, null)
                    .stream()
                    .map(JpaGameEntity::toDomain)
                    .toList();
        }

        return categories.stream()
                .flatMap(category ->
                        jpaGameRepository.filterGames(minPriceAmount, maxPriceAmount, category.name())
                                .stream()
                                .map(JpaGameEntity::toDomain)
                )
                .distinct()
                .toList();
    }

    @Override
    public List<Game> findGamesByIds(List<GameId> ids) {
        List<UUID> gameIds = ids.stream()
                .map(GameId::id)
                .toList();
        return jpaGameRepository.findAllByIds(gameIds).stream()
                .map(JpaGameEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(GameId gameId) {
        return jpaGameRepository.existsById(gameId.id());
    }


}

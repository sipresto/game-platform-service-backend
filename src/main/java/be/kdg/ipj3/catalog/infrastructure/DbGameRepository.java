package be.kdg.ipj3.catalog.infrastructure;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameEntity;
import be.kdg.ipj3.catalog.infrastructure.jpa.IGameRepository;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.GameId;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class DbGameRepository implements GameRepository {
    private final IGameRepository gameRepo;

    public DbGameRepository(IGameRepository gameRepo) {
        this.gameRepo = gameRepo;
    }

    @Override
    public void save(Game game) {
        gameRepo.save(JpaGameEntity.fromDomain(game));
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepo.findAll()
                .stream().map(JpaGameEntity::toDomain).toList();
    }

    @Override
    public List<Game> filterGames(Money minPrice, Money maxPrice, List<Category> categories) {
        BigDecimal minPriceAmount = minPrice != null ? minPrice.amount() : null;
        BigDecimal maxPriceAmount = maxPrice != null ? maxPrice.amount() : null;

        if (categories == null || categories.isEmpty()) {
            return gameRepo.filterGames(minPriceAmount, maxPriceAmount, null)
                    .stream()
                    .map(JpaGameEntity::toDomain)
                    .toList();
        }

        return categories.stream()
                .flatMap(category ->
                        gameRepo.filterGames(minPriceAmount, maxPriceAmount, category.name())
                                .stream()
                                .map(JpaGameEntity::toDomain)
                )
                .distinct()
                .toList();
    }

    @Override
    public Optional<Game> getGameById(GameId gameId) {
        return gameRepo.findById(gameId.id()).map(JpaGameEntity::toDomain);
    }
}
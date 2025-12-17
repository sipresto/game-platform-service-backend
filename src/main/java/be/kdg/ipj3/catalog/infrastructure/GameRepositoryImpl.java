package be.kdg.ipj3.catalog.infrastructure;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaAchievementEntity;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameEntity;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameRepository;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GameRepositoryImpl implements GameRepository {
    private final JpaGameRepository jpaGameRepository;

    public GameRepositoryImpl(JpaGameRepository jpaGameRepository) {
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
    public Optional<String> findBackendUrlByGameId(GameId id) {
        return jpaGameRepository.findBackendUrlById(id.id());
    }

    @Override
    public Optional<String> findFrontendUrlByGameId(GameId id) {
        return jpaGameRepository.findFrontendUrlById(id.id());
    }

    @Override
    public List<Game> filterGames(Money minPrice, Money maxPrice, List<Category> categories) {
        BigDecimal minPriceAmount = minPrice != null ? minPrice.amount() : null;
        BigDecimal maxPriceAmount = maxPrice != null ? maxPrice.amount() : null;

        return jpaGameRepository.filterGames(minPriceAmount, maxPriceAmount, categories)
                .stream()
                .map(JpaGameEntity::toDomain)
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

    @Override
    public void deleteAll() {
        jpaGameRepository.deleteAll();
    }

    @Override
    public List<Achievement> findAchievementsByGameId(GameId id) {
        return jpaGameRepository.findAchievementsByGameId(id.id()).stream()
                .map(JpaAchievementEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Achievement> findAchievementByCode(String code) {
        return jpaGameRepository.findAchievementByCode(code).map(JpaAchievementEntity::toDomain);
    }

    @Override
    public Optional<Game> findByIdWithAchievements(GameId id) {
        return jpaGameRepository.findByIdWithAchievements(id.id()).map(JpaGameEntity::toDomain);
    }
}

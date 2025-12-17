package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository {
    void save(Game game);

    Optional<Game> findById(GameId id);

    List<Game> findAllGames();

    Optional<String> findBackendUrlByGameId(GameId id);

    Optional<String> findFrontendUrlByGameId(GameId id);

    List<Game> filterGames(Money minPrice, Money maxPrice, List<Category> categories);

    List<Game> findGamesByIds(List<GameId> ids);

    boolean existsById(GameId id);

    void deleteAll();

    List<Achievement> findAchievementsByGameId(GameId id);

    Optional<Achievement> findAchievementByGameIdAndCode(GameId gameId, String code);

    Optional<Game> findByIdWithAchievements(GameId id);
}

package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IGameRepository {
    void save(Game game);

    Optional<Game> findById(GameId gameId);

    List<Game> findAllGames();

    Optional<String> findServiceUrlByGameId(GameId id);

    List<Game> filterGames(Money minPrice, Money maxPrice, List<Category> categories);

    List<Game> findGamesByIds(List<GameId> ids);

    boolean existsById(GameId gameId);

    void deleteAll();
}
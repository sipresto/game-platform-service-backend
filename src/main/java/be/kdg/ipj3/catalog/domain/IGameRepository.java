package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.GameId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IGameRepository {
    void save(Game game);

    List<Game> getAllGames();

    Optional<Game> getGameById(GameId gameId);

    List<Game> filterGames(Money minPrice, Money maxPrice, List<Category> categories);
}
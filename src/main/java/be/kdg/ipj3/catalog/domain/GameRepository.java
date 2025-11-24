package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.Money;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository {
    void save(Game game);

    List<Game> getAllGames();

    List<Game> filterGames(Money minPrice, Money maxPrice, List<Category> categories);
}
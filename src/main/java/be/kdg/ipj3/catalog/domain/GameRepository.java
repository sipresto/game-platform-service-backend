package be.kdg.ipj3.catalog.domain;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository {
    void save(Game game);

    List<Game> getAllGames();
}

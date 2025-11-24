package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.GameId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IGameRepository {
    void save(Game game);

    List<Game> findAllGames();

    Optional<String> findServiceUrlByGameId(GameId id);
}

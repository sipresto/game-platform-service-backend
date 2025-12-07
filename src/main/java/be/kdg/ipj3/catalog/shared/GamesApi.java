package be.kdg.ipj3.catalog.shared;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Game;
import org.springframework.modulith.NamedInterface;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NamedInterface
public interface GamesApi {
    List<Game> findAllGames();

    Game findGameById(UUID id);

    List<Game> findGamesByIds(List<UUID> ids);

    boolean existsById(UUID id);

    List<Achievement> findAchievementsByGameId(UUID game);

    Optional<Achievement> findAchievementByCode(String code);

    String findServiceByGameId(UUID id);

}

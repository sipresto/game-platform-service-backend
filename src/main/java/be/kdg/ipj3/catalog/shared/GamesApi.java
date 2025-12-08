package be.kdg.ipj3.catalog.shared;

import be.kdg.ipj3.catalog.domain.Game;
import org.springframework.modulith.NamedInterface;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NamedInterface
public interface GamesApi {
    List<Game> findAllGames();

    Game findGameById(UUID id);

    boolean existsById(UUID id);

    List<AchievementDto> findAchievementDtosByGameId(UUID game);

    Optional<AchievementDto> findAchievementDtoByCode(String code);

    String findServiceByGameId(UUID id);

    Optional<GamePurchaseDto> findGameDtoById(UUID id);
}

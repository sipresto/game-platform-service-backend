package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.shared.domain.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class GameService implements GamesApi {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> findAllGames() {
        return gameRepository.findAllGames();
    }

    public String findServiceByGameId(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findServiceUrlByGameId(gameId)
                .orElseThrow(gameId::notFound);
    }

    public List<Game> filterGames(Money minPrice, Money maxPrice,
                                  List<Category> categories) {
        return gameRepository.filterGames(minPrice, maxPrice, categories);
    }

    public Game findGameById(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findById(gameId)
                .orElseThrow(gameId::notFound);
    }

    public List<Game> findGamesByIds(List<UUID> ids) {
        List<GameId> gameIds = ids.stream()
                .map(GameId::new)
                .toList();
        return gameRepository.findGamesByIds(gameIds);
    }

    public boolean existsById(UUID id) {
        var gameId = new GameId(id);
        if (gameRepository.existsById(gameId)) {
            return true;
        } else {
            throw gameId.notFound();
        }
    }

    public List<Achievement> findAchievementsByGameId(UUID id) {
        var gameId = new GameId(id);
        return gameRepository.findAchievementsByGameId(gameId);
    }

    public Optional<Achievement> findAchievementByCode(String code) {
        return gameRepository.findAchievementByCode(code);
    }
}
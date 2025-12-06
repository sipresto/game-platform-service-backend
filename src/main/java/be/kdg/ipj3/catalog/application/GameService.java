package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.shared.domain.Money;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    private final IGameRepository gameRepository;

    public GameService(
            @Qualifier("gameRepository") final IGameRepository gameRepository
    ) {
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public List<Game> findAllGames() {
        return gameRepository.findAllGames();
    }

    @Transactional(readOnly = true)
    public String findServiceByGameId(GameId id) {
        return gameRepository.findServiceUrlByGameId(id)
                .orElseThrow(id::notFound);
    }

    @Transactional(readOnly = true)
    public List<Game> filterGames(Money minPrice, Money maxPrice,
                                  List<Category> categories) {
        return gameRepository.filterGames(minPrice, maxPrice, categories);
    }

    @Transactional(readOnly = true)
    public Game findGameById(GameId gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(gameId::notFound);
    }

    @Transactional(readOnly = true)
    public List<Game> findGamesByIds(List<GameId> ids) {
        return gameRepository.findGamesByIds(ids);
    }

    public boolean existsById(GameId gameId) {
        if (gameRepository.existsById(gameId)) {
            return true;
        }
        else {
            throw gameId.notFound();
        }
    }

    public List<Achievement> findAchievementsByGameId(GameId game) {
        return gameRepository.findAchievementsByGameId(game);
    }

    public Optional<Achievement> findAchievementByCode(String code) {
        return gameRepository.findAchievementByCode(code);
    }
}
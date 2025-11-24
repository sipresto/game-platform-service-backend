package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import be.kdg.ipj3.shared.domain.Money;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameService {
    private final IGameRepository IGameRepository;

    public GameService(
            @Qualifier("dbIGameRepository") final IGameRepository IGameRepository
    ) {
        this.IGameRepository = IGameRepository;
    }

    @Transactional(readOnly = true)
    public List<Game> getAllGames() {
        return IGameRepository.getAllGames();
    }

    @Transactional(readOnly = true)
    public List<Game> filterGames(Money minPrice, Money maxPrice,
                                  List<Category> categories) {
        return IGameRepository.filterGames(minPrice, maxPrice, categories);
    }

    @Transactional(readOnly = true)
    public Game getGameById(GameId gameId) {
        return IGameRepository.getGameById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game with id " + gameId + " not found"));
    }
}
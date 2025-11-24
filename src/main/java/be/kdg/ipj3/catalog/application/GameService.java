package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(
            @Qualifier("dbGameRepository") final GameRepository gameRepository
    ) {
        this.gameRepository = gameRepository;
    }

    @Transactional
    public List<Game> getAllGames() {
        return gameRepository.getAllGames();
    }

    public Game getGameById(GameId gameId) {
        return gameRepository.getGameById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game with id " + gameId + " not found"));
    }
}

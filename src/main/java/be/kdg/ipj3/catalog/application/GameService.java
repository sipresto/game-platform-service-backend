package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(
            @Qualifier("dbGameRepository") final GameRepository gameRepository
    ) {
        this.gameRepository = gameRepository;
    }


    public List<Game> getGames() {
        return gameRepository.getAllGames();
    }
}

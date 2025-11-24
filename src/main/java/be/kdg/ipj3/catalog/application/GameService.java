package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameService {
    private final IGameRepository gameRepository;

    public GameService(
            @Qualifier("gameRepository") final IGameRepository gameRepository
    ) {
        this.gameRepository = gameRepository;
    }

    @Transactional
    public List<Game> findAllGames() {
        return gameRepository.findAllGames();
    }

    @Transactional
    public String findServiceByGameId(GameId id) {
        return gameRepository.findServiceUrlByGameId(id)
                .orElseThrow(id::notFound);
    }
}

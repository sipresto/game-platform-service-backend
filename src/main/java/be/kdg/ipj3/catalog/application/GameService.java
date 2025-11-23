package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.shared.domain.Money;
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

    @Transactional(readOnly = true)
    public List<Game> getAllGames() {
        return gameRepository.getAllGames();
    }

    @Transactional(readOnly = true)
    public List<Game> filterGames(Money minPrice, Money maxPrice,
                                  List<Category> categories, Boolean hasAchievements) {
        return gameRepository.getAllGames().stream()
                .filter(game -> hasAchievements == null ||
                        (hasAchievements != game.getAchievements().isEmpty()))
                .filter(game -> categories == null || categories.isEmpty() ||
                        categories.stream().anyMatch(game.getCategories()::contains))
                .filter(game -> minPrice == null || game.getPrice().amount().compareTo(minPrice.amount()) >= 0)
                .filter(game -> maxPrice == null || game.getPrice().amount().compareTo(maxPrice.amount()) <= 0)
                .toList();
    }
}

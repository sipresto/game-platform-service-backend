package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GameServiceTest {

    @Mock
    private IGameRepository gameRepository;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(gameRepository);
    }

    @Test
    void testFindAllGames() {
        // Arrange
        Game game = new Game("Game1", "Description", Money.of(BigDecimal.valueOf(4.99)), List.of(Category.BOARDGAME), "http://localhost/api/game");
        when(gameRepository.findAllGames()).thenReturn(List.of(game));

        // Act
        List<Game> games = gameService.findAllGames();

        // Assert
        assertEquals(1, games.size());
        assertEquals(game, games.get(0));
    }
}

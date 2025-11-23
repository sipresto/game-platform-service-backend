package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    private GameService gameService;

    private Game gameWithAchievements;
    private Game gameWithoutAchievements;
    private Game cheapGame;
    private Game expensiveGame;
    private Game boardGame;
    private Game strategyGame;
    private Game multiCategoryGame;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(gameRepository);

        GameId gameWithAchievementsId = GameId.create();
        GameId boardGameId = GameId.create();
        GameId strategyGameId = GameId.create();
        GameId multiCategoryGameId = GameId.create();

        gameWithAchievements = new Game(
                gameWithAchievementsId, "Game with Achievements", "Description",
                Money.of(BigDecimal.valueOf(19.99)),
                List.of(new Achievement("Win", "Win the game", 10, gameWithAchievementsId)),
                List.of(Category.STRATEGY)
        );

        gameWithoutAchievements = new Game(
                "Game without Achievements", "Description",
                Money.of(BigDecimal.valueOf(9.99)),
                List.of(Category.BOARDGAME)
        );

        cheapGame = new Game(
                "Cheap Game", "Description",
                Money.of(BigDecimal.valueOf(1.99)),
                List.of(Category.BOARDGAME)
        );

        expensiveGame = new Game(
                "Expensive Game", "Description",
                Money.of(BigDecimal.valueOf(59.99)),
                List.of(Category.EXTERNAL)
        );

        boardGame = new Game(
                boardGameId, "Board Game", "Description",
                Money.of(BigDecimal.valueOf(24.99)),
                List.of(),
                List.of(Category.BOARDGAME)
        );

        strategyGame = new Game(
                strategyGameId, "Strategy Game", "Description",
                Money.of(BigDecimal.valueOf(29.99)),
                List.of(),
                List.of(Category.STRATEGY)
        );

        multiCategoryGame = new Game(
                multiCategoryGameId, "Multi Category Game", "Description",
                Money.of(BigDecimal.valueOf(39.99)),
                List.of(new Achievement("Multi", "Multi achievement", 15, multiCategoryGameId)),
                List.of(Category.BOARDGAME, Category.STRATEGY)
        );
    }

    @Test
    void testGetAllGames() {
        // Arrange
        List<Game> expectedGames = List.of(gameWithAchievements, gameWithoutAchievements);
        when(gameRepository.getAllGames()).thenReturn(expectedGames);

        // Act
        List<Game> actualGames = gameService.getAllGames();

        // Assert
        assertEquals(2, actualGames.size());
        assertEquals(expectedGames, actualGames);
    }

    @Test
    void testFilterGames_AllParametersNull_ReturnsAllGames() {
        // Arrange
        List<Game> allGames = List.of(gameWithAchievements, gameWithoutAchievements, cheapGame, expensiveGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, null, null);

        // Assert
        assertEquals(4, result.size());
        assertTrue(result.containsAll(allGames));
    }

    @Test
    void testFilterGames_ByHasAchievementsTrue() {
        // Arrange
        List<Game> allGames = List.of(gameWithAchievements, gameWithoutAchievements, multiCategoryGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, null, true);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(gameWithAchievements));
        assertTrue(result.contains(multiCategoryGame));
        assertFalse(result.contains(gameWithoutAchievements));
    }

    @Test
    void testFilterGames_ByHasAchievementsFalse() {
        // Arrange
        List<Game> allGames = List.of(gameWithAchievements, gameWithoutAchievements, boardGame, strategyGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, null, false);

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains(gameWithoutAchievements));
        assertTrue(result.contains(boardGame));
        assertTrue(result.contains(strategyGame));
        assertFalse(result.contains(gameWithAchievements));
    }

    @Test
    void testFilterGames_ByHasAchievementsNull_NoFilterApplied() {
        // Arrange
        List<Game> allGames = List.of(gameWithAchievements, gameWithoutAchievements);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, null, null);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(allGames));
    }

    @Test
    void testFilterGames_BySingleCategory() {
        // Arrange
        List<Game> allGames = List.of(boardGame, strategyGame, multiCategoryGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, List.of(Category.BOARDGAME), null);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(boardGame));
        assertTrue(result.contains(multiCategoryGame));
        assertFalse(result.contains(strategyGame));
    }

    @Test
    void testFilterGames_ByMultipleCategories() {
        // Arrange
        List<Game> allGames = List.of(boardGame, strategyGame, multiCategoryGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, List.of(Category.BOARDGAME, Category.STRATEGY), null);

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.containsAll(allGames));
    }

    @Test
    void testFilterGames_ByCategoriesEmptyList_NoFilterApplied() {
        // Arrange
        List<Game> allGames = List.of(boardGame, strategyGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, List.of(), null);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(allGames));
    }

    @Test
    void testFilterGames_ByCategoriesNull_NoFilterApplied() {
        // Arrange
        List<Game> allGames = List.of(boardGame, strategyGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, null, null);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(allGames));
    }

    @Test
    void testFilterGames_ByMinPrice() {
        // Arrange
        Game boundaryGame = new Game(
                "Boundary Game", "Description",
                Money.of(BigDecimal.valueOf(10.00)),
                List.of(Category.STRATEGY)
        );

        List<Game> allGames = List.of(cheapGame, boundaryGame, expensiveGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);
        Money minPrice = Money.of(BigDecimal.valueOf(10.00));

        // Act
        List<Game> result = gameService.filterGames(minPrice, null, null, null);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(boundaryGame));
        assertTrue(result.contains(expensiveGame));
        assertFalse(result.contains(cheapGame));
    }

    @Test
    void testFilterGames_ByMaxPrice() {
        // Arrange
        List<Game> allGames = List.of(cheapGame, gameWithoutAchievements, expensiveGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);
        Money maxPrice = Money.of(BigDecimal.valueOf(20.00));

        // Act
        List<Game> result = gameService.filterGames(null, maxPrice, null, null);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(cheapGame));
        assertTrue(result.contains(gameWithoutAchievements));
        assertFalse(result.contains(expensiveGame));
    }

    @Test
    void testFilterGames_ByPriceRange() {
        // Arrange
        List<Game> allGames = List.of(cheapGame, gameWithoutAchievements, expensiveGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);
        Money minPrice = Money.of(BigDecimal.valueOf(5.00));
        Money maxPrice = Money.of(BigDecimal.valueOf(30.00));

        // Act
        List<Game> result = gameService.filterGames(minPrice, maxPrice, null, null);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(gameWithoutAchievements));
        assertFalse(result.contains(cheapGame));
        assertFalse(result.contains(expensiveGame));
    }

    @Test
    void testFilterGames_CombinedFilters() {
        // Arrange
        List<Game> allGames = List.of(gameWithAchievements, gameWithoutAchievements, boardGame, strategyGame, multiCategoryGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        Money minPrice = Money.of(BigDecimal.valueOf(20.00));
        Money maxPrice = Money.of(BigDecimal.valueOf(40.00));

        // Act
        List<Game> result = gameService.filterGames(
                minPrice, maxPrice,
                List.of(Category.BOARDGAME, Category.STRATEGY),
                true
        );

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(multiCategoryGame));
    }

    @Test
    void testFilterGames_NoMatchingResults() {
        // Arrange
        List<Game> allGames = List.of(cheapGame, gameWithoutAchievements);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        Money minPrice = Money.of(BigDecimal.valueOf(50.00));
        Money maxPrice = Money.of(BigDecimal.valueOf(100.00));

        // Act
        List<Game> result = gameService.filterGames(minPrice, maxPrice, List.of(Category.BOARDGAME), true);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterGames_ExactPriceMatch() {
        // Arrange
        Game exactPriceGame = new Game(
                "Exact Price Game", "Description",
                Money.of(BigDecimal.valueOf(25.00)),
                List.of(Category.STRATEGY)
        );

        List<Game> allGames = List.of(exactPriceGame, cheapGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        Money exactPrice = Money.of(BigDecimal.valueOf(25.00));

        // Act
        List<Game> result = gameService.filterGames(exactPrice, exactPrice, null, null);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(exactPriceGame));
        assertFalse(result.contains(cheapGame));
    }

    @Test
    void testFilterGames_MinPriceGreaterThanMaxPrice_ReturnsEmpty() {
        // Arrange
        List<Game> allGames = List.of(cheapGame, expensiveGame);
        when(gameRepository.getAllGames()).thenReturn(allGames);

        Money minPrice = Money.of(BigDecimal.valueOf(50.00));
        Money maxPrice = Money.of(BigDecimal.valueOf(10.00));

        // Act
        List<Game> result = gameService.filterGames(minPrice, maxPrice, null, null);

        // Assert
        assertTrue(result.isEmpty());
    }
}
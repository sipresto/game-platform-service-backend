package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameServiceTest {

    @Mock
    private IGameRepository IGameRepository;

    private GameService gameService;

    private Game gameWithAchievements;
    private Game gameWithoutAchievements;
    private Game cheapGame;
    private Game expensiveGame;
    private Game boardGame;
    private Game strategyGame;
    private Game multiCategoryGame;
    private GameId gameWithAchievementsId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameService(IGameRepository);

        gameWithAchievementsId = GameId.create();
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
        when(IGameRepository.getAllGames()).thenReturn(expectedGames);

        // Act
        List<Game> actualGames = gameService.getAllGames();

        // Assert
        assertEquals(2, actualGames.size());
        assertEquals(expectedGames, actualGames);
        verify(IGameRepository).getAllGames();
    }

    @Test
    void testFilterGames_AllParametersNull_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(gameWithAchievements, gameWithoutAchievements, cheapGame, expensiveGame);
        when(IGameRepository.filterGames(null, null, null)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, null);

        // Assert
        assertEquals(4, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(null, null, null);
    }

    @Test
    void testFilterGames_BySingleCategory_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(boardGame, multiCategoryGame);
        when(IGameRepository.filterGames(null, null, List.of(Category.BOARDGAME))).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, List.of(Category.BOARDGAME));

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(null, null, List.of(Category.BOARDGAME));
    }

    @Test
    void testFilterGames_ByMultipleCategories_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(boardGame, strategyGame, multiCategoryGame);
        when(IGameRepository.filterGames(null, null, List.of(Category.BOARDGAME, Category.STRATEGY))).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, List.of(Category.BOARDGAME, Category.STRATEGY));

        // Assert
        assertEquals(3, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(null, null, List.of(Category.BOARDGAME, Category.STRATEGY));
    }

    @Test
    void testFilterGames_ByEmptyCategories_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(boardGame, strategyGame);
        when(IGameRepository.filterGames(null, null, List.of())).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, List.of());

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(null, null, List.of());
    }

    @Test
    void testFilterGames_ByMinPrice_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(gameWithAchievements, expensiveGame);
        Money minPrice = Money.of(BigDecimal.valueOf(10.00));
        when(IGameRepository.filterGames(minPrice, null, null)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(minPrice, null, null);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(minPrice, null, null);
    }

    @Test
    void testFilterGames_ByMaxPrice_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(cheapGame, gameWithoutAchievements);
        Money maxPrice = Money.of(BigDecimal.valueOf(20.00));
        when(IGameRepository.filterGames(null, maxPrice, null)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(null, maxPrice, null);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(null, maxPrice, null);
    }

    @Test
    void testFilterGames_ByPriceRange_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(gameWithoutAchievements);
        Money minPrice = Money.of(BigDecimal.valueOf(5.00));
        Money maxPrice = Money.of(BigDecimal.valueOf(30.00));
        when(IGameRepository.filterGames(minPrice, maxPrice, null)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(minPrice, maxPrice, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(minPrice, maxPrice, null);
    }

    @Test
    void testFilterGames_CombinedPriceAndCategoryFilters_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(multiCategoryGame);
        Money minPrice = Money.of(BigDecimal.valueOf(20.00));
        Money maxPrice = Money.of(BigDecimal.valueOf(40.00));
        List<Category> categories = List.of(Category.BOARDGAME, Category.STRATEGY);
        when(IGameRepository.filterGames(minPrice, maxPrice, categories)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(minPrice, maxPrice, categories);

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(minPrice, maxPrice, categories);
    }

    @Test
    void testFilterGames_NoMatchingResults_ReturnsEmptyList() {
        // Arrange
        List<Game> expectedGames = List.of();
        Money minPrice = Money.of(BigDecimal.valueOf(50.00));
        Money maxPrice = Money.of(BigDecimal.valueOf(100.00));
        List<Category> categories = List.of(Category.BOARDGAME);
        when(IGameRepository.filterGames(minPrice, maxPrice, categories)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(minPrice, maxPrice, categories);

        // Assert
        assertTrue(result.isEmpty());
        verify(IGameRepository).filterGames(minPrice, maxPrice, categories);
    }

    @Test
    void testFilterGames_ExactPriceMatch_DelegatesToRepository() {
        // Arrange
        Game exactPriceGame = new Game(
                "Exact Price Game", "Description",
                Money.of(BigDecimal.valueOf(25.00)),
                List.of(Category.STRATEGY)
        );

        List<Game> expectedGames = List.of(exactPriceGame);
        Money exactPrice = Money.of(BigDecimal.valueOf(25.00));
        when(IGameRepository.filterGames(exactPrice, exactPrice, null)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(exactPrice, exactPrice, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(exactPrice, exactPrice, null);
    }

    @Test
    void testFilterGames_InvalidPriceRange_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of();
        Money minPrice = Money.of(BigDecimal.valueOf(50.00));
        Money maxPrice = Money.of(BigDecimal.valueOf(10.00));
        when(IGameRepository.filterGames(minPrice, maxPrice, null)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(minPrice, maxPrice, null);

        // Assert
        assertTrue(result.isEmpty());
        verify(IGameRepository).filterGames(minPrice, maxPrice, null);
    }

    @Test
    void testFilterGames_OnlyCategoriesFilter_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(boardGame, multiCategoryGame);
        List<Category> categories = List.of(Category.BOARDGAME);
        when(IGameRepository.filterGames(null, null, categories)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(null, null, categories);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(null, null, categories);
    }

    @Test
    void testFilterGames_OnlyMinPriceFilter_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(gameWithAchievements, expensiveGame);
        Money minPrice = Money.of(BigDecimal.valueOf(15.00));
        when(IGameRepository.filterGames(minPrice, null, null)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(minPrice, null, null);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(minPrice, null, null);
    }

    @Test
    void testFilterGames_OnlyMaxPriceFilter_DelegatesToRepository() {
        // Arrange
        List<Game> expectedGames = List.of(cheapGame, gameWithoutAchievements);
        Money maxPrice = Money.of(BigDecimal.valueOf(15.00));
        when(IGameRepository.filterGames(null, maxPrice, null)).thenReturn(expectedGames);

        // Act
        List<Game> result = gameService.filterGames(null, maxPrice, null);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedGames, result);
        verify(IGameRepository).filterGames(null, maxPrice, null);
    }
    @Test
    void testGetGameById_Found() {
        // Arrange
        GameId id = gameWithAchievements.getId(); // any game is fine
        when(IGameRepository.getGameById(id)).thenReturn(Optional.of(gameWithAchievements));

        // Act
        Game result = gameService.getGameById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(IGameRepository).getGameById(id);
    }

    @Test
    void testGetGameById_NotFound() {
        // Arrange
        GameId id = GameId.create();
        when(IGameRepository.getGameById(id)).thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> gameService.getGameById(id)
        );

        assertEquals("Game with id " + id + " not found", ex.getMessage());
        verify(IGameRepository).getGameById(id);
    }

}
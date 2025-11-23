package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameRestControllerTest {

    @Mock
    private GameService gameService;

    private GameRestController gameRestController;

    private Game testGame;
    private GameDto testGameDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameRestController = new GameRestController(gameService);

        testGame = new Game(
                GameId.create(),
                "Test Game",
                "Test Description",
                Money.of(BigDecimal.valueOf(19.99)),
                List.of(),
                List.of(Category.STRATEGY)
        );
        testGameDto = GameDto.from(testGame);
    }

    @Test
    void testGetAllGames_ReturnsGames() {
        // Arrange
        when(gameService.getAllGames()).thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGames();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testGameDto, response.getBody().getFirst());
        verify(gameService).getAllGames();
    }

    @Test
    void testGetAllGames_NoGamesFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(gameService.getAllGames()).thenReturn(List.of());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> gameRestController.getAllGames()
        );
        assertEquals("No games found", exception.getMessage());
        verify(gameService).getAllGames();
    }

    @Test
    void testGetAllGamesByFilter_WithAllFilters_ReturnsFilteredGames() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                Money.of(BigDecimal.valueOf(10.00)),
                Money.of(BigDecimal.valueOf(30.00)),
                List.of(Category.STRATEGY),
                false
        );
        when(gameService.filterGames(
                filterRequest.minPrice(),
                filterRequest.maxPrice(),
                filterRequest.categories(),
                filterRequest.hasAchievements()
        )).thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testGameDto, response.getBody().getFirst());
        verify(gameService).filterGames(
                filterRequest.minPrice(),
                filterRequest.maxPrice(),
                filterRequest.categories(),
                filterRequest.hasAchievements()
        );
    }

    @Test
    void testGetAllGamesByFilter_WithNullFilters_ReturnsAllGames() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(null, null, null, false);
        when(gameService.filterGames(null, null, null, false)).thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(gameService).filterGames(null, null, null, false);
    }

    @Test
    void testGetAllGamesByFilter_WithPriceFilterOnly_ReturnsMatchingGames() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                Money.of(BigDecimal.valueOf(10.00)),
                Money.of(BigDecimal.valueOf(30.00)),
                null,
                false
        );
        when(gameService.filterGames(filterRequest.minPrice(), filterRequest.maxPrice(), null, false))
                .thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(gameService).filterGames(filterRequest.minPrice(), filterRequest.maxPrice(), null, false);
    }

    @Test
    void testGetAllGamesByFilter_WithCategoryFilterOnly_ReturnsMatchingGames() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                null,
                List.of(Category.STRATEGY, Category.BOARDGAME),
                false
        );
        when(gameService.filterGames(null, null, filterRequest.categories(), false))
                .thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(gameService).filterGames(null, null, filterRequest.categories(), false);
    }

    @Test
    void testGetAllGamesByFilter_WithAchievementsFilterTrue_ReturnsGamesWithAchievements() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                null,
                null,
                true
        );
        when(gameService.filterGames(null, null, null, true))
                .thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(gameService).filterGames(null, null, null, true);
    }

    @Test
    void testGetAllGamesByFilter_WithAchievementsFilterFalse_ReturnsGamesWithoutAchievements() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                null,
                null,
                false
        );
        when(gameService.filterGames(null, null, null, false))
                .thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(gameService).filterGames(null, null, null, false);
    }

    @Test
    void testGetAllGamesByFilter_NoMatchingGames_ThrowsResourceNotFoundException() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                Money.of(BigDecimal.valueOf(100.00)),
                null,
                null,
                false
        );
        when(gameService.filterGames(
                filterRequest.minPrice(),
                filterRequest.maxPrice(),
                filterRequest.categories(),
                filterRequest.hasAchievements()
        )).thenReturn(List.of());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> gameRestController.getAllGamesByFilter(filterRequest)
        );
        assertEquals("No games found", exception.getMessage());
        verify(gameService).filterGames(
                filterRequest.minPrice(),
                filterRequest.maxPrice(),
                filterRequest.categories(),
                filterRequest.hasAchievements()
        );
    }

    @Test
    void testGetAllGamesByFilter_WithEmptyCategories_ReturnsGames() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                null,
                List.of(),
                false
        );
        when(gameService.filterGames(null, null, List.of(), false))
                .thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(gameService).filterGames(null, null, List.of(), false);
    }

    @Test
    void testGetAllGamesByFilter_MultipleGamesReturned() {
        // Arrange
        Game anotherGame = new Game(
                GameId.create(),
                "Another Game",
                "Another Description",
                Money.of(BigDecimal.valueOf(29.99)),
                List.of(),
                List.of(Category.BOARDGAME)
        );
        GameDto anotherGameDto = GameDto.from(anotherGame);

        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                null,
                List.of(Category.STRATEGY, Category.BOARDGAME),
                false
        );
        when(gameService.filterGames(null, null, filterRequest.categories(), false))
                .thenReturn(List.of(testGame, anotherGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(testGameDto));
        assertTrue(response.getBody().contains(anotherGameDto));
        verify(gameService).filterGames(null, null, filterRequest.categories(), false);
    }

    @Test
    void testGetAllGamesByFilter_WithZeroPrice_ReturnsGames() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                Money.of(BigDecimal.valueOf(0.00)),
                Money.of(BigDecimal.valueOf(10.00)),
                null,
                false
        );
        when(gameService.filterGames(filterRequest.minPrice(), filterRequest.maxPrice(), null, false))
                .thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(gameService).filterGames(filterRequest.minPrice(), filterRequest.maxPrice(), null, false);
    }

    @Test
    void testGetAllGamesByFilter_WithOnlyMinPrice_ReturnsGames() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                Money.of(BigDecimal.valueOf(15.00)),
                null,
                null,
                false
        );
        when(gameService.filterGames(filterRequest.minPrice(), null, null, false))
                .thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(gameService).filterGames(filterRequest.minPrice(), null, null, false);
    }

    @Test
    void testGetAllGamesByFilter_WithOnlyMaxPrice_ReturnsGames() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                Money.of(BigDecimal.valueOf(25.00)),
                null,
                false
        );
        when(gameService.filterGames(null, filterRequest.maxPrice(), null, false))
                .thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(gameService).filterGames(null, filterRequest.maxPrice(), null, false);
    }

    @Test
    void testGetAllGamesByFilter_WithSingleCategory_ReturnsGames() {
        // Arrange
        GameFilterRequest filterRequest = new GameFilterRequest(
                null,
                null,
                List.of(Category.STRATEGY),
                false
        );
        when(gameService.filterGames(null, null, filterRequest.categories(), false))
                .thenReturn(List.of(testGame));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGamesByFilter(filterRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(gameService).filterGames(null, null, filterRequest.categories(), false);
    }
}
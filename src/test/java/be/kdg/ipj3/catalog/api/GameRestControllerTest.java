package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GameRestControllerTest {

    @Mock
    private GameService gameService;

    private GameRestController gameRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameRestController = new GameRestController(gameService);
    }

    @Test
    void testGetAllGames_ReturnsGames() {
        // Arrange
        Game game = new Game("Game1", "Description",  Money.of(BigDecimal.valueOf(19.99)), List.of(Category.BOARDGAME));
        GameDto gameDto = GameDto.from(game);
        when(gameService.getAllGames()).thenReturn(List.of(game));

        // Act
        ResponseEntity<List<GameDto>> response = gameRestController.getAllGames();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(gameDto, response.getBody().get(0));
    }

    @Test
    void testGetAllGames_NoGamesFound() {
        // Arrange
        when(gameService.getAllGames()).thenReturn(List.of());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> gameRestController.getAllGames());
    }
}

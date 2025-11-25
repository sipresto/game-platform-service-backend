package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class GameServiceTests {

    @Mock
    private IGameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void findAllGames_shouldReturnListOfGames_whenGamesExist() {
        // Arrange
        Game game1 = new Game("Test Game", "Test Description", Money.of(BigDecimal.valueOf(49.99)), "http://test.com/service", List.of(Category.CARDGAME));
        Game game2 = new Game("Test Game 2", "Test Description 2", Money.of(BigDecimal.valueOf(39.99)), "http://test.com/service2", List.of(Category.CARDGAME));
        List<Game> expectedGames = List.of(game1, game2);

        given(gameRepository.findAllGames()).willReturn(expectedGames);

        // Act
        List<Game> actualGames = gameService.findAllGames();

        // Assert
        assertNotNull(actualGames);
        assertEquals(2, actualGames.size());
        assertEquals(expectedGames, actualGames);
        then(gameRepository).should().findAllGames();
    }

    @Test
    void findAllGames_shouldReturnEmptyList_whenNoGamesExist() {
        // Arrange
        given(gameRepository.findAllGames()).willReturn(List.of());

        // Act
        List<Game> actualGames = gameService.findAllGames();

        // Assert
        assertNotNull(actualGames);
        assertTrue(actualGames.isEmpty());
        then(gameRepository).should().findAllGames();
    }

    @Test
    void findServiceByGameId_shouldReturnUrl_whenGameExists() {
        // Arrange
        GameId gameId = GameId.create();
        String expectedUrl = "http://test.com/service";

        given(gameRepository.findServiceUrlByGameId(gameId)).willReturn(Optional.of(expectedUrl));

        // Act
        String serviceUrl = gameService.findServiceByGameId(gameId);

        // Assert
        assertNotNull(serviceUrl);
        assertEquals(expectedUrl, serviceUrl);
        then(gameRepository).should().findServiceUrlByGameId(gameId);
    }

    @Test
    void findServiceByGameId_shouldThrowNotFoundException_whenGameDoesNotExist() {
        // Arrange
        GameId gameId = GameId.create();

        given(gameRepository.findServiceUrlByGameId(gameId)).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> gameService.findServiceByGameId(gameId));
        then(gameRepository).should().findServiceUrlByGameId(gameId);
    }
}

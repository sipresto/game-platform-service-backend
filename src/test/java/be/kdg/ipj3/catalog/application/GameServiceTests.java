package be.kdg.ipj3.catalog.application;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class GameServiceTests {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void findAllGames_shouldReturnListOfGames_whenGamesExist() {
        // Arrange
        Game game1 = new Game(GameId.create(), "Test Game", "Test Description", Money.of(BigDecimal.valueOf(49.99)), "http://test.com/service", "A rule", List.of(), List.of(Category.CARDGAME), "", Instant.now());
        Game game2 = new Game(GameId.create(), "Test Game 2", "Test Description 2", Money.of(BigDecimal.valueOf(39.99)), "http://test.com/service2", "A rule", List.of(), List.of(Category.CARDGAME), "", Instant.now());
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
    void findBackendUrlByGameId_shouldReturnUrl_whenGameExists() {
        // Arrange
        UUID gameId = UUID.randomUUID();
        String expectedUrl = "http://test.com/service";

        given(gameRepository.findBackendUrlByGameId(new GameId(gameId))).willReturn(Optional.of(expectedUrl));

        // Act
        String serviceUrl = gameService.findBackendUrlByGameId(gameId);

        // Assert
        assertNotNull(serviceUrl);
        assertEquals(expectedUrl, serviceUrl);
        then(gameRepository).should().findBackendUrlByGameId(new GameId(gameId));
    }

    @Test
    void findBackendUrlByGameId_shouldThrowNotFoundException_whenGameDoesNotExist() {
        // Arrange
        UUID gameId = UUID.randomUUID();

        given(gameRepository.findBackendUrlByGameId(new GameId(gameId))).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> gameService.findBackendUrlByGameId(gameId));
        then(gameRepository).should().findBackendUrlByGameId(new GameId(gameId));
    }
}

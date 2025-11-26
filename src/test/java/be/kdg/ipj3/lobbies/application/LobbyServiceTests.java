package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.lobbies.api.dto.GameCreatedResponseDto;
import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto;
import be.kdg.ipj3.lobbies.domain.exception.GameServiceUnavailableException;
import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.lobbies.infrastructure.rest.GameClient;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTests {

    @Mock
    private GameClient gameClient;

    @InjectMocks
    private LobbyService lobbyService;

    @Test
    void startGame_shouldReturnGameUrl_whenGameStartsSuccessfully() {
        // Arrange
        String serviceUrl = "http://test.com/game-service";
        UserId player1 = UserId.create();
        UserId player2 = UserId.create();
        List<UserId> players = List.of(player1, player2);
        Difficulty difficulty = Difficulty.EASY;
        String expectedGameUrl = "http://test.com/game/123";

        GameCreatedResponseDto responseDto = new GameCreatedResponseDto(expectedGameUrl);
        given(gameClient.startGame(eq(serviceUrl), any(GameStartRequestDto.class))).willReturn(responseDto);

        // Act
        String actualGameUrl = lobbyService.startGame(serviceUrl, players, difficulty);

        // Assert
        assertNotNull(actualGameUrl);
        assertEquals(expectedGameUrl, actualGameUrl);
        then(gameClient).should().startGame(eq(serviceUrl), any(GameStartRequestDto.class));
    }

    @Test
    void startGame_shouldThrowGameServiceUnavailableException_whenGameClientThrowsException() {
        // Arrange
        String serviceUrl = "http://test.com/game-service";
        UserId player1 = UserId.create();
        List<UserId> players = List.of(player1);
        Difficulty difficulty = Difficulty.EASY;

        given(gameClient.startGame(eq(serviceUrl), any(GameStartRequestDto.class)))
                .willThrow(new RuntimeException("Connection failed"));

        // Act & Assert
        assertThrows(GameServiceUnavailableException.class,
                () -> lobbyService.startGame(serviceUrl, players, difficulty));
        then(gameClient).should().startGame(eq(serviceUrl), any(GameStartRequestDto.class));
    }
}
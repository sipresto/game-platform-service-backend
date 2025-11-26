package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.lobbies.api.dto.GameCreatedResponseDto;
import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto;
import be.kdg.ipj3.lobbies.domain.exception.GameServiceUnavailableException;
import be.kdg.ipj3.lobbies.domain.lobby.*;
import be.kdg.ipj3.lobbies.infrastructure.rest.GameClient;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTests {

    @Mock
    private GameClient gameClient;

    @Mock
    private ILobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;

    @Test
    void createLobby_shouldSaveAndReturnLobby() {
        // Arrange
        String password = "pass";
        int maxPlayers = 4;
        GameId gameId = new GameId(UUID.randomUUID());
        UserId host = UserId.create();

        // Act
        Lobby result = lobbyService.createLobby(password, maxPlayers, gameId, host);

        // Assert
        assertNotNull(result);
        assertEquals(host, result.getHost());
        assertEquals(maxPlayers, result.getMaxPlayers());
        then(lobbyRepository).should().save(any(Lobby.class));
    }

    @Test
    void joinLobby_shouldAddPlayerAndSave() {
        // Arrange
        LobbyId lobbyId = LobbyId.create();
        UserId player = UserId.create();
        UserId host = UserId.create();
        String password = "pass";
        GameId gameId = new GameId(UUID.randomUUID());

        Lobby existingLobby = new Lobby(lobbyId, password, 4, gameId, host);

        given(lobbyRepository.findById(lobbyId)).willReturn(Optional.of(existingLobby));

        // Act
        lobbyService.joinLobby(lobbyId, player, password);

        // Assert
        assertTrue(existingLobby.getPlayers().contains(player));
        then(lobbyRepository).should().save(existingLobby);
    }

    @Test
    void getAllOpenLobbies_shouldReturnListFromRepo() {
        // Arrange
        given(lobbyRepository.findByStatus(LobbyStatus.OPEN)).willReturn(List.of());

        // Act
        lobbyService.getAllOpenLobbies();

        // Assert
        then(lobbyRepository).should().findByStatus(LobbyStatus.OPEN);
    }

    @Test
    void startGame_shouldReturnGameUrl_whenGameStartsSuccessfully() {
        // Arrange
        String serviceUrl = "http://test.com/game-service";
        LobbyId lobbyId = LobbyId.create();
        UserId host = UserId.create();
        Difficulty difficulty = Difficulty.EASY;
        String expectedGameUrl = "http://test.com/game/123";

        Lobby lobby = new Lobby(lobbyId, "pass", 4, new GameId(UUID.randomUUID()), host);
        given(lobbyRepository.findById(lobbyId)).willReturn(Optional.of(lobby));

        GameCreatedResponseDto responseDto = new GameCreatedResponseDto(expectedGameUrl);
        given(gameClient.startGame(eq(serviceUrl), any(GameStartRequestDto.class))).willReturn(responseDto);

        // Act
        String actualGameUrl = lobbyService.startGame(serviceUrl, lobbyId.id().toString(), host, difficulty);

        // Assert
        assertNotNull(actualGameUrl);
        assertEquals(expectedGameUrl, actualGameUrl);
        assertEquals(LobbyStatus.IN_GAME, lobby.getStatus());

        then(gameClient).should().startGame(eq(serviceUrl), any(GameStartRequestDto.class));
        then(lobbyRepository).should(times(1)).save(lobby);
    }

    @Test
    void startGame_shouldThrowException_whenUserIsNotHost() {
        // Arrange
        String serviceUrl = "http://test.com/game-service";
        LobbyId lobbyId = LobbyId.create();
        UserId host = UserId.create();
        UserId notHost = UserId.create();
        Difficulty difficulty = Difficulty.EASY;

        Lobby lobby = new Lobby(lobbyId, "pass", 4, new GameId(UUID.randomUUID()), host);
        given(lobbyRepository.findById(lobbyId)).willReturn(Optional.of(lobby));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> lobbyService.startGame(serviceUrl, lobbyId.id().toString(), notHost, difficulty));

        then(gameClient).shouldHaveNoInteractions();
    }

    @Test
    void startGame_shouldThrowGameServiceUnavailableException_whenGameClientThrowsException() {
        // Arrange
        String serviceUrl = "http://test.com/game-service";
        LobbyId lobbyId = LobbyId.create();
        UserId host = UserId.create();
        Difficulty difficulty = Difficulty.EASY;

        Lobby lobby = new Lobby(lobbyId, "pass", 4, new GameId(UUID.randomUUID()), host);
        given(lobbyRepository.findById(lobbyId)).willReturn(Optional.of(lobby));

        given(gameClient.startGame(eq(serviceUrl), any(GameStartRequestDto.class)))
                .willThrow(new RuntimeException("Connection failed"));

        // Act & Assert
        assertThrows(GameServiceUnavailableException.class,
                () -> lobbyService.startGame(serviceUrl, lobbyId.id().toString(), host, difficulty));

        then(gameClient).should().startGame(eq(serviceUrl), any(GameStartRequestDto.class));
    }
}
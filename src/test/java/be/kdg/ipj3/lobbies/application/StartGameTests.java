package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto;
import be.kdg.ipj3.lobbies.api.dto.GameCreatedResponseDto;
import be.kdg.ipj3.lobbies.domain.exception.GameServiceUnavailableException;
import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyStatus;
import be.kdg.ipj3.lobbies.domain.lobby.ILobbyRepository;
import be.kdg.ipj3.lobbies.infrastructure.rest.GameClient;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StartGameTests {

    @Mock
    private ILobbyRepository lobbyRepository;

    @Mock
    private GameClient gameClient;

    @InjectMocks
    private LobbyService lobbyService;

    @Captor
    private ArgumentCaptor<Lobby> lobbyCaptor;

    private Lobby lobby;
    private LobbyId lobbyId;
    private final GameId gameId = new GameId(UUID.randomUUID());
    private UserId host;

    @BeforeEach
    void setUp() {
        host = new UserId(UUID.randomUUID());
        lobbyId = new LobbyId(UUID.randomUUID());
        lobby = new Lobby(
                lobbyId,
                "",
                4,
                gameId,
                host,
                List.of(host),
                LobbyStatus.OPEN,
                null
        );
    }

    @Test
    void startGame_shouldCallGameClientAndMarkLobbyInGame_whenGameClientSucceeds() {
        // Arrange
        given(lobbyRepository.findById(lobbyId)).willReturn(Optional.of(lobby));

        String serviceUrl = "http://gameservice";
        String returnedUrl = "http://game/123";

        GameCreatedResponseDto mockResponse = new GameCreatedResponseDto(returnedUrl);

        given(gameClient.startGame(eq(serviceUrl), any(GameStartRequestDto.class)))
                .willReturn(mockResponse);

        // Act
        String gameUrl = lobbyService.startGame(serviceUrl, lobbyId, host, null);

        // Assert
        assertThat(gameUrl).isEqualTo(returnedUrl);
        then(lobbyRepository).should().save(lobbyCaptor.capture());
        Lobby saved = lobbyCaptor.getValue();
        assertThat(saved.getStatus()).isEqualTo(LobbyStatus.IN_GAME);
        assertThat(saved.getActiveGameUrl()).isEqualTo(returnedUrl);
    }

    @Test
    void startGame_shouldThrowGameServiceUnavailable_whenGameClientThrows() {
        // Arrange
        given(lobbyRepository.findById(lobbyId)).willReturn(Optional.of(lobby));
        given(gameClient.startGame(anyString(), any(GameStartRequestDto.class)))
                .willThrow(new RuntimeException("down"));

        // Act & Assert
        assertThatThrownBy(() ->
                lobbyService.startGame("url", lobbyId, host, null)
        ).isInstanceOf(GameServiceUnavailableException.class)
                .hasMessage("Game service is currently unavailable.");
    }

    @Test
    void startGame_shouldThrow_whenLobbyNotFound() {
        // Arrange
        given(lobbyRepository.findById(lobbyId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                lobbyService.startGame("url", lobbyId, host, null)
        ).isInstanceOf(RuntimeException.class);
    }
}

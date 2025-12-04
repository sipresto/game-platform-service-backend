package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyStatus;
import be.kdg.ipj3.lobbies.domain.lobby.ILobbyRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class JoinLobbyTests {

    @Mock
    private ILobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;

    @Captor
    private ArgumentCaptor<Lobby> lobbyCaptor;

    private final GameId dummyGame = new GameId(UUID.randomUUID());

    private Lobby existingLobby;
    private LobbyId existingLobbyId;
    private final String correctPassword = "pw";
    private final UserId host = new UserId(UUID.randomUUID());

    @BeforeEach
    void setUp() {
        existingLobbyId = new LobbyId(UUID.randomUUID());
        existingLobby = new Lobby(
                existingLobbyId,
                correctPassword,
                4,
                dummyGame,
                host,
                List.of(host),
                LobbyStatus.OPEN,
                null
        );
    }

    @Test
    void joinLobby_shouldAddPlayerAndSave_whenPasswordMatchesAndLobbyExists() {
        // Arrange
        UserId newPlayer = new UserId(UUID.randomUUID());
        given(lobbyRepository.findById(existingLobbyId)).willReturn(Optional.of(existingLobby));

        // Act
        lobbyService.joinLobby(existingLobbyId, newPlayer, correctPassword);

        // Assert
        then(lobbyRepository).should().save(lobbyCaptor.capture());
        Lobby saved = lobbyCaptor.getValue();
        assertThat(saved.getPlayers()).contains(newPlayer);
    }

    @Test
    void joinLobby_shouldThrow_whenLobbyNotFound() {
        // Arrange
        UserId newPlayer = new UserId(UUID.randomUUID());
        LobbyId nonExisting = new LobbyId(UUID.randomUUID());
        given(lobbyRepository.findById(nonExisting)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> lobbyService.joinLobby(nonExisting, newPlayer, "whatever"))
                .isInstanceOf(RuntimeException.class);
    }
}

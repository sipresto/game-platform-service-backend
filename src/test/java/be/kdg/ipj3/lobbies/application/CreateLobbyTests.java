package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyStatus;
import be.kdg.ipj3.lobbies.infrastructure.rest.GameClient;
import be.kdg.ipj3.lobbies.domain.lobby.ILobbyRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateLobbyTests {

    @Mock
    private GameClient gameClient;

    @Mock
    private ILobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;

    @Test
    void createLobby_shouldCreateAndSaveLobby() {
        // Arrange
        String password = "secret";
        int maxPlayers = 4;
        GameId gameId = new GameId(UUID.randomUUID());
        UserId hostId = new UserId(UUID.randomUUID());

        // Act
        Lobby lobby = lobbyService.createLobby(password, maxPlayers, gameId, hostId);

        // Assert
        assertThat(lobby.getGame()).isEqualTo(gameId);
        assertThat(lobby.getHost()).isEqualTo(hostId);
        assertThat(lobby.getMaxPlayers()).isEqualTo(maxPlayers);
        assertThat(lobby.getPassword()).isEqualTo(password);
        assertThat(lobby.getStatus()).isEqualTo(LobbyStatus.OPEN);
        then(lobbyRepository).should().save(lobby);
    }
}

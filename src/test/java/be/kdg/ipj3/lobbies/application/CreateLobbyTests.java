package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyStatus;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.shared.UserId;
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
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;

    @Test
    void createLobby_shouldCreateAndSaveLobby() {
        // Arrange
        String password = "secret";
        int maxPlayers = 4;
        UUID gameUUID = UUID.randomUUID();
        GameId gameId = new GameId(gameUUID);
        String keycloakId = "keycloakId";
        UserId hostId = new UserId(UUID.randomUUID());

        // Act
        Lobby lobby = lobbyService.createLobby(keycloakId, password, maxPlayers, gameUUID);

        // Assert
        assertThat(lobby.getGame()).isEqualTo(gameId);
        assertThat(lobby.getHost()).isEqualTo(hostId);
        assertThat(lobby.getMaxPlayers()).isEqualTo(maxPlayers);
        assertThat(lobby.getPassword()).isEqualTo(password);
        assertThat(lobby.getStatus()).isEqualTo(LobbyStatus.OPEN);
        then(lobbyRepository).should().save(lobby);
    }
}

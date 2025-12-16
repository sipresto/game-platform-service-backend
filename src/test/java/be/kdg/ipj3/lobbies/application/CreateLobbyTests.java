package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyStatus;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateLobbyTests {
    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private UsersApi usersApi;

    @Mock
    private GamesApi gamesApi;

    @InjectMocks
    private LobbyService lobbyService;

    @Test
    void createLobby_shouldCreateAndSaveLobby() {
        // Arrange
        String password = "secret";
        int maxPlayers = 4;
        UUID gameUUID = UUID.randomUUID();
        GameId expectedGameId = new GameId(gameUUID);

        String keycloakId = "keycloakId";
        UserId expectedHostId = new UserId(UUID.randomUUID());

        User mockUser = new User(
                expectedHostId,
                keycloakId,
                "someUsername",
                List.of(),
                0,
                null
        );

        Game mockGame = new Game(
                expectedGameId,
                "Some Game",
                "Description",
                Money.of(new BigDecimal(100)),
                "http://example.service",
                "Some rules",
                List.of(),
                List.of(),
                "",
                Instant.now()
        );

        // Mock API calls
        when(usersApi.findByKeycloakId(keycloakId)).thenReturn(mockUser);
        when(gamesApi.findGameById(gameUUID)).thenReturn(mockGame);

        // Act
        Lobby lobby = lobbyService.createLobby(keycloakId, password, maxPlayers, gameUUID);

        // Assert
        assertThat(lobby.getGame()).isEqualTo(expectedGameId);
        assertThat(lobby.getHost()).isEqualTo(expectedHostId);
        assertThat(lobby.getMaxPlayers()).isEqualTo(maxPlayers);
        assertThat(lobby.getPassword()).isEqualTo(password);
        assertThat(lobby.getStatus()).isEqualTo(LobbyStatus.OPEN);

        then(lobbyRepository).should().save(lobby);
    }
}

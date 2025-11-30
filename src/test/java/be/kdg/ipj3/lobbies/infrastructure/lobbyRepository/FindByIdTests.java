package be.kdg.ipj3.lobbies.infrastructure.lobbyRepository;

import be.kdg.ipj3.lobbies.domain.lobby.Lobby;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyId;
import be.kdg.ipj3.lobbies.domain.lobby.LobbyStatus;
import be.kdg.ipj3.lobbies.infrastructure.LobbyRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(LobbyRepository.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class FindByIdTests {

    @Autowired
    LobbyRepository lobbyRepository;

    @Test
    void findById_shouldReturnLobby_whenLobbyExists() {
        // Arrange
        LobbyId lobbyId = LobbyId.create();
        GameId gameId = GameId.create();
        UserId hostId = UserId.create();
        UserId playerId = UserId.create();

        Lobby lobby = new Lobby(
                lobbyId,
                "password123",
                4,
                gameId,
                hostId,
                List.of(hostId, playerId),
                LobbyStatus.OPEN,
                null
        );
        lobbyRepository.save(lobby);

        // Act
        Optional<Lobby> result = lobbyRepository.findById(lobbyId);

        // Assert
        assertThat(result).isPresent();
        Lobby foundLobby = result.orElseThrow();

        assertThat(foundLobby.getPassword()).isEqualTo("password123");
        assertThat(foundLobby.getMaxPlayers()).isEqualTo(4);
        assertThat(foundLobby.getGame().id()).isEqualTo(gameId.id());
        assertThat(foundLobby.getHost().id()).isEqualTo(hostId.id());
        assertThat(foundLobby.getPlayers().size()).isEqualTo(2);
        assertThat(foundLobby.getStatus()).isEqualTo(LobbyStatus.OPEN);
        assertThat(foundLobby.getActiveGameUrl()).isNull();
    }

    @Test
    void findById_shouldReturnEmpty_whenLobbyDoesNotExist() {
        // Arrange
        LobbyId nonExistentId = LobbyId.create();

        // Act
        Optional<Lobby> result = lobbyRepository.findById(nonExistentId);

        // Assert
        assertThat(result).isEmpty();
    }
}

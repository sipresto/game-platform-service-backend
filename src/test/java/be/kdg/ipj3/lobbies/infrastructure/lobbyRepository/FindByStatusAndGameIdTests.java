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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(LobbyRepository.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class FindByStatusAndGameIdTests {

    @Autowired
    LobbyRepository lobbyRepository;

    private void createTestLobby(GameId gameId, LobbyStatus lobbyStatus) {
        UserId hostId = UserId.create();

        Lobby lobby = new Lobby(
                LobbyId.create(),
                "password",
                2,
                gameId,
                hostId,
                List.of(hostId),
                lobbyStatus,
                ""
        );
        lobbyRepository.save(lobby);
    }

    @Test
    void findByStatusAndGameId_shouldReturnMatchingLobbies_whenLobbiesExist() {
        // Arrange
        GameId gameId1 = GameId.create();
        GameId gameId2 = GameId.create();

        createTestLobby(gameId1, LobbyStatus.OPEN);
        createTestLobby(gameId1, LobbyStatus.OPEN);
        createTestLobby(gameId1, LobbyStatus.CLOSED);
        createTestLobby(gameId2, LobbyStatus.OPEN);

        // Act
        List<Lobby> result = lobbyRepository.findByStatusAndGameId(LobbyStatus.OPEN, gameId1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.stream().allMatch(l ->
                l.getStatus() == LobbyStatus.OPEN && l.getGame().id().equals(gameId1.id())
        )).isTrue();
    }

    @Test
    void findByStatusAndGameId_shouldReturnEmptyList_whenNoLobbiesMatch() {
        // Arrange
        GameId gameId1 = GameId.create();
        GameId gameId2 = GameId.create();

        createTestLobby(gameId1, LobbyStatus.OPEN);
        createTestLobby(gameId1, LobbyStatus.CLOSED);

        // Act
        List<Lobby> result = lobbyRepository.findByStatusAndGameId(LobbyStatus.OPEN, gameId2);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
    }
}

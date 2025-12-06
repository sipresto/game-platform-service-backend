package be.kdg.ipj3.lobbies.infrastructure.lobbyRepository;

import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyId;
import be.kdg.ipj3.lobbies.domain.LobbyStatus;
import be.kdg.ipj3.lobbies.infrastructure.LobbyRepositoryImpl;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(LobbyRepositoryImpl.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class FindByStatusAndGameIdTests {

    @Autowired
    LobbyRepositoryImpl lobbyRepositoryImpl;

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
        lobbyRepositoryImpl.save(lobby);
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
        List<Lobby> result = lobbyRepositoryImpl.findByStatusAndGameId(LobbyStatus.OPEN, gameId1);

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
        List<Lobby> result = lobbyRepositoryImpl.findByStatusAndGameId(LobbyStatus.OPEN, gameId2);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
    }
}

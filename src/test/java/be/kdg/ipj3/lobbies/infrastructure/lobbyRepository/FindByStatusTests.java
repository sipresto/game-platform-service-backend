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
public class FindByStatusTests {

    @Autowired
    LobbyRepositoryImpl lobbyRepositoryImpl;

    private void createTestLobby(LobbyStatus lobbyStatus) {
        UserId hostId = UserId.create();

        Lobby lobby = new Lobby(
                LobbyId.create(),
                "password",
                2,
                GameId.create(),
                hostId,
                List.of(hostId),
                lobbyStatus,
                ""
        );
        lobbyRepositoryImpl.save(lobby);
    }

    @Test
    void findByStatus_shouldReturnLobbies_whenLobbiesWithStatusExist() {
        // Arrange
        createTestLobby(LobbyStatus.OPEN);
        createTestLobby(LobbyStatus.OPEN);
        createTestLobby(LobbyStatus.CLOSED);
        createTestLobby(LobbyStatus.IN_GAME);

        // Act
        List<Lobby> result = lobbyRepositoryImpl.findByStatus(LobbyStatus.OPEN);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.stream().allMatch(l -> l.getStatus() == LobbyStatus.OPEN)).isTrue();
    }

    @Test
    void findByStatus_shouldReturnEmptyList_whenNoLobbiesWithStatusExist() {
        // Arrange
        createTestLobby(LobbyStatus.CLOSED);
        createTestLobby(LobbyStatus.IN_GAME);

        // Act
        List<Lobby> result = lobbyRepositoryImpl.findByStatus(LobbyStatus.OPEN);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
    }
}

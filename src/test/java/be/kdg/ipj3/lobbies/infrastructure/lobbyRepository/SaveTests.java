package be.kdg.ipj3.lobbies.infrastructure.lobbyRepository;

import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyId;
import be.kdg.ipj3.lobbies.domain.LobbyStatus;
import be.kdg.ipj3.lobbies.infrastructure.LobbyRepositoryImpl;
import be.kdg.ipj3.lobbies.infrastructure.jpa.JpaLobbyEntity;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(LobbyRepositoryImpl.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class SaveTests {

    @Autowired
    LobbyRepositoryImpl lobbyRepositoryImpl;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void save_shouldPersistLobby_whenLobbyIsValid() {
        // Arrange
        LobbyId lobbyId = LobbyId.create();
        GameId gameId = GameId.create();
        UserId hostId = UserId.create();
        UserId playerId = UserId.create();

        Lobby newLobby = new Lobby(
                lobbyId,
                "password123",
                4,
                gameId,
                hostId,
                List.of(hostId, playerId),
                LobbyStatus.OPEN,
                null
        );

        // Act
        lobbyRepositoryImpl.save(newLobby);

        // Assert
        JpaLobbyEntity foundLobby = entityManager.find(JpaLobbyEntity.class, lobbyId.id());
        assertThat(foundLobby).isNotNull();
        assertThat(foundLobby.getPassword()).isEqualTo("password123");
        assertThat(foundLobby.getMaxPlayers()).isEqualTo(4);
        assertThat(foundLobby.getGameId()).isEqualTo(gameId.id());
        assertThat(foundLobby.getHostId()).isEqualTo(hostId.id());
        assertThat(foundLobby.getPlayers().size()).isEqualTo(2);
        assertThat(foundLobby.getStatus()).isEqualTo("OPEN");
        assertThat(foundLobby.getActiveGameUrl()).isNull();
    }

    @Test
    void save_shouldUpdateLobby_whenLobbyAlreadyExists() {
        // Arrange
        LobbyId lobbyId = LobbyId.create();
        GameId gameId = GameId.create();
        UserId hostId = UserId.create();
        UserId playerId = UserId.create();

        Lobby originalLobby = new Lobby(
                lobbyId,
                "oldpassword",
                4,
                gameId,
                hostId,
                List.of(hostId),
                LobbyStatus.OPEN,
                null
        );
        lobbyRepositoryImpl.save(originalLobby);

        Lobby updatedLobby = new Lobby(
                lobbyId,
                "newpassword",
                4,
                gameId,
                playerId, // new host
                List.of(hostId, playerId),
                LobbyStatus.IN_GAME,
                "http://game.com/session/123"
        );

        // Act
        lobbyRepositoryImpl.save(updatedLobby);

        // Assert
        JpaLobbyEntity foundLobby = entityManager.find(JpaLobbyEntity.class, lobbyId.id());
        assertThat(foundLobby).isNotNull();
        assertThat(foundLobby.getPassword()).isEqualTo("newpassword");
        assertThat(foundLobby.getHostId()).isEqualTo(playerId.id());
        assertThat(foundLobby.getPlayers().size()).isEqualTo(2);
        assertThat(foundLobby.getStatus()).isEqualTo("IN_GAME");
        assertThat(foundLobby.getActiveGameUrl()).isEqualTo("http://game.com/session/123");
    }
}

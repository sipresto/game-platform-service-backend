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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FindOpenLobbiesTests {

    @Mock
    private LobbyRepository lobbyRepository;

    @InjectMocks
    private LobbyService lobbyService;

    @Test
    void findAllOpenLobbies_shouldReturnOpenLobbies() {
        // Arrange
        Lobby l1 = new Lobby("", 4, new GameId(UUID.randomUUID()), new UserId(UUID.randomUUID()));
        Lobby l2 = new Lobby("", 2, new GameId(UUID.randomUUID()), new UserId(UUID.randomUUID()));
        given(lobbyRepository.findByStatus(LobbyStatus.OPEN)).willReturn(List.of(l1, l2));

        // Act
        List<Lobby> result = lobbyService.findAllOpenLobbies();

        // Assert
        assertThat(result).containsExactly(l1, l2);
    }

    @Test
    void findOpenLobbiesForGame_shouldReturnLobbiesOfGivenGame() {
        // Arrange
        GameId gameId = new GameId(UUID.randomUUID());
        Lobby l1 = new Lobby("", 4, gameId, new UserId(UUID.randomUUID()));
        Lobby l2 = new Lobby("", 2, gameId, new UserId(UUID.randomUUID()));
        given(lobbyRepository.findByStatusAndGameId(LobbyStatus.OPEN, gameId))
                .willReturn(List.of(l1, l2));

        // Act
        List<Lobby> result = lobbyService.findOpenLobbiesForGame(gameId);

        // Assert
        assertThat(result).containsExactly(l1, l2);
    }
}

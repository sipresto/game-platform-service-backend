//package be.kdg.ipj3.lobbies.application;
//
//import be.kdg.ipj3.lobbies.domain.LobbyRepository;
//import be.kdg.ipj3.lobbies.domain.Lobby;
//import be.kdg.ipj3.lobbies.domain.LobbyId;
//import be.kdg.ipj3.lobbies.domain.LobbyStatus;
//import be.kdg.ipj3.catalog.shared.GameId;
//import be.kdg.ipj3.users.shared.UserId;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.then;
//
//@ExtendWith(MockitoExtension.class)
//class JoinLobbyTests {
//
//    @Mock
//    private LobbyRepository lobbyRepository;
//
//    @InjectMocks
//    private LobbyService lobbyService;
//
//    @Captor
//    private ArgumentCaptor<Lobby> lobbyCaptor;
//
//    private final GameId dummyGame = new GameId(UUID.randomUUID());
//
//    private UUID existingLobbyUUID;
//    private Lobby existingLobby;
//    private LobbyId existingLobbyId;
//    private final String correctPassword = "pw";
//    private final UserId host = new UserId(UUID.randomUUID());
//
//    @BeforeEach
//    void setUp() {
//        existingLobbyUUID = UUID.randomUUID();
//        existingLobbyId = new LobbyId(existingLobbyUUID);
//        existingLobby = new Lobby(
//                existingLobbyId,
//                correctPassword,
//                4,
//                dummyGame,
//                host,
//                List.of(host),
//                LobbyStatus.OPEN,
//                null
//        );
//    }
//
//    @Test
//    void joinLobby_shouldAddPlayerAndSave_whenPasswordMatchesAndLobbyExists() {
//        // Arrange
//        String keycloakId = "keycloakId";
//        UserId newPlayer = new UserId(UUID.randomUUID());
//        given(lobbyRepository.findById(existingLobbyId)).willReturn(Optional.of(existingLobby));
//
//        // Act
//        lobbyService.joinLobby(existingLobbyUUID, keycloakId, correctPassword);
//
//        // Assert
//        then(lobbyRepository).should().save(lobbyCaptor.capture());
//        Lobby saved = lobbyCaptor.getValue();
//        assertThat(saved.getPlayers()).contains(newPlayer);
//    }
//
//    @Test
//    void joinLobby_shouldThrow_whenLobbyNotFound() {
//        // Arrange
//        String keycloakId = "keycloakId";
//        UUID nonExistingUUID = UUID.randomUUID();
//        LobbyId nonExisting = new LobbyId(nonExistingUUID);
//        given(lobbyRepository.findById(nonExisting)).willReturn(Optional.empty());
//
//        // Act & Assert
//        assertThatThrownBy(() -> lobbyService.joinLobby(nonExistingUUID, keycloakId, "whatever"))
//                .isInstanceOf(RuntimeException.class);
//    }
//}

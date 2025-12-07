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
//class LeaveLobbyTests {
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
//    private Lobby lobby;
//    private UUID lobbyUUID;
//    private LobbyId lobbyId;
//    private String keycloakId;
//    private UserId player;
//    private UserId host;
//
//    @BeforeEach
//    void setUp() {
//        lobbyUUID = UUID.randomUUID();
//        lobbyId = new LobbyId(lobbyUUID);
//        keycloakId = "keycloak-123";
//        player = new UserId(UUID.randomUUID());
//        host = new UserId(UUID.randomUUID());
//        lobby = new Lobby(
//                lobbyId,
//                "",
//                4,
//                new GameId(UUID.randomUUID()),
//                host,
//                List.of(host),
//                LobbyStatus.OPEN,
//                null
//        );
//        lobby.join(player, lobby.getPassword());
//    }
//
//    @Test
//    void leaveLobby_shouldRemovePlayerAndSave_whenLobbyExists() {
//        // Arrange
//        given(lobbyRepository.findById(lobbyId)).willReturn(Optional.of(lobby));
//
//        // Act
//        lobbyService.leaveLobby(lobbyUUID, keycloakId);
//
//        // Assert
//        then(lobbyRepository).should().save(lobbyCaptor.capture());
//        Lobby saved = lobbyCaptor.getValue();
//        assertThat(saved.getPlayers()).doesNotContain(player);
//    }
//
//    @Test
//    void leaveLobby_shouldThrow_whenLobbyNotFound() {
//        // Arrange
//        LobbyId nonExisting = new LobbyId(UUID.randomUUID());
//        given(lobbyRepository.findById(nonExisting)).willReturn(Optional.empty());
//
//        // Act & Assert
//        assertThatThrownBy(() -> lobbyService.leaveLobby(lobbyUUID, keycloakId))
//                .isInstanceOf(RuntimeException.class);
//    }
//}

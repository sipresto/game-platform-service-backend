package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyId;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.lobbies.domain.LobbyStatus;
import be.kdg.ipj3.lobbies.domain.exceptions.GameServiceUnavailableException;
import be.kdg.ipj3.lobbies.infrastructure.rest.GameClient;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LobbyService {
    private final GameClient gameClient;
    private final LobbyRepository lobbyRepository;
    private final GamesApi gamesApi;
    private final UsersApi usersApi;

    public LobbyService(GameClient gameClient, LobbyRepository lobbyRepository, GamesApi gamesApi, UsersApi usersApi) {
        this.gameClient = gameClient;
        this.lobbyRepository = lobbyRepository;
        this.gamesApi = gamesApi;
        this.usersApi = usersApi;
    }

    public Lobby createLobby(String keycloakId, String password, int maxPlayers, UUID gameIdParam) {
        var hostId = usersApi.findByKeycloakId(keycloakId).getId();

        var gameId = gamesApi.findGameById(gameIdParam).getId();

        Lobby lobby = new Lobby(password, maxPlayers, gameId, hostId);

        lobbyRepository.save(lobby);

        return lobby;
    }

    public void joinLobby(UUID id, String keycloakId, String password) {

        var playerId = usersApi.findByKeycloakId(keycloakId).getId();
        var lobbyId = new LobbyId(id);
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        lobby.join(playerId, password);

        lobbyRepository.save(lobby);
    }

    public String startGame(UUID id, String requestorKeycloakId, Difficulty difficulty) {

        var requestor = usersApi.findByKeycloakId(requestorKeycloakId).getId();
        var lobbyId = new LobbyId(id);

        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);
        var gameId = lobby.getGame().id();

        String serviceUrl = gamesApi.findServiceByGameId(gameId);

        lobby.validateReadyToStart(requestor);

        List<UUID> playerIds = lobby.getPlayers().stream()
                .map(UserId::id)
                .toList();

        GameStartRequestDto dto = new GameStartRequestDto(playerIds, difficulty);

        try {
            String gameUrl = gameClient.startGame(serviceUrl, dto).gameUrl();
            lobby.markAsInGame(gameUrl);
            lobbyRepository.save(lobby);
            return gameUrl;
        } catch (Exception e) {
            throw new GameServiceUnavailableException("Game service is currently unavailable.");
        }
    }

    public List<Lobby> findAllOpenLobbies() {
        return lobbyRepository.findByStatus(LobbyStatus.OPEN);
    }

    public List<Lobby> findOpenLobbiesForGame(UUID id) {

        var gameId = gamesApi.findGameById(id).getId();

        return lobbyRepository.findByStatusAndGameId(LobbyStatus.OPEN, gameId);
    }

    public void leaveLobby(UUID id, String keycloakId) {

        var playerId = usersApi.findByKeycloakId(keycloakId).getId();
        var lobbyId = new LobbyId(id);

        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        lobby.leave(playerId);
        lobbyRepository.save(lobby);
    }
}

package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto;
import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto.PlatformPlayerDto;
import be.kdg.ipj3.lobbies.api.dto.LobbyDetailDto;
import be.kdg.ipj3.lobbies.api.dto.PlayerDto;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.domain.LobbyId;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.lobbies.domain.LobbyStatus;
import be.kdg.ipj3.lobbies.shared.GameServiceUnavailableException;
import be.kdg.ipj3.lobbies.infrastructure.rest.GameClient;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.lobbies.shared.LobbyApi;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.sessions.shared.SessionsApi;
import be.kdg.ipj3.users.shared.SmallUserDto;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@Transactional
public class LobbyService implements LobbyApi {
    private final GameClient gameClient;
    private final LobbyRepository lobbyRepository;
    private final GamesApi gamesApi;
    private final UsersApi usersApi;
    private final SessionsApi sessionsApi;
    private final Logger logger = Logger.getLogger(LobbyService.class.getName());

    public LobbyService(GameClient gameClient, LobbyRepository lobbyRepository, GamesApi gamesApi, UsersApi usersApi,
                        SessionsApi sessionsApi) {
        this.gameClient = gameClient;
        this.lobbyRepository = lobbyRepository;
        this.gamesApi = gamesApi;
        this.usersApi = usersApi;
        this.sessionsApi = sessionsApi;
    }

    public Lobby createLobby(String keycloakId, String password, int maxPlayers, UUID gameIdParam) {
        var hostId = usersApi.findIdByKeycloakId(keycloakId);

        var gameId = gamesApi.findGameById(gameIdParam).getId();

        Lobby lobby = new Lobby(password, maxPlayers, gameId, hostId);

        lobbyRepository.save(lobby);

        return lobby;
    }

    public void joinLobby(UUID id, String keycloakId, String password) {

        var playerId = usersApi.findIdByKeycloakId(keycloakId);
        var lobbyId = new LobbyId(id);
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        lobby.join(playerId, password);

        lobbyRepository.save(lobby);
    }

    public Lobby findLobbyById(UUID id) {
        var lobbyId = new LobbyId(id);
        return lobbyRepository.findById(lobbyId).orElseThrow(lobbyId::notFound);
    }


    public LobbyDetailDto getLobbyDetails(UUID lobbyId, String keycloakId) {
        usersApi.findIdByKeycloakId(keycloakId);
        Lobby lobby = findLobbyById(lobbyId);

        List<PlayerDto> playerDtos = lobby.getPlayers().stream()
                .map(userId -> {
                   var user = usersApi.findSmallUserDtoById(userId.id());

                    String username = (user != null) ? user.username() : "Unknown";

                    return new PlayerDto(
                            userId.id().toString(),
                            username
                    );
                })
                .toList();

        return LobbyDetailDto.fromDomain(lobby, playerDtos);
    }

    public void ResetLobbyTimer(UUID activeGameId){
        var lobby = lobbyRepository.findByGameId(activeGameId);
        if(lobby.isEmpty()){
            logger.warning("Lobby with active game id " + activeGameId + " is empty");
        }
        else{
            var presentLobby = lobby.get();
            presentLobby.resetTimer();
            lobbyRepository.save(presentLobby);
        }
    }

    public String startGame(UUID id, String requestorKeycloakId, Difficulty difficulty) {
        var requestor = usersApi.findIdByKeycloakId(requestorKeycloakId);
        var lobbyId = new LobbyId(id);

        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        lobby.validateReadyToStart(requestor);

        var gameId = lobby.getGame().id();
        String serviceUrl = gamesApi.findBackendUrlByGameId(gameId);

        Set<UserId> playerIds = lobby.getPlayers();
        List<PlatformPlayerDto> players = new ArrayList<>();
        for (UserId userId : playerIds) {
            SmallUserDto smallUserDto = usersApi.findSmallUserDtoById(userId.id());
            PlatformPlayerDto playerDto = PlatformPlayerDto.fromDto(smallUserDto);

            players.add(playerDto);
        }

        SessionId sessionId = SessionId.create();
        GameStartRequestDto dto = new GameStartRequestDto(sessionId.id(), players, difficulty);

        try {
            gameClient.startGame(serviceUrl, dto);
            String gameUrl = gamesApi.findFrontendUrlByGameId(gameId) + "/" + sessionId.id();

            List<UUID> playerList = lobby.getPlayers().stream()
                    .map(UserId::id)
                    .toList();
            sessionsApi.createSession(sessionId.id(), gameId, playerList, difficulty);

            lobby.startGame(gameUrl);
            lobbyRepository.save(lobby);

            return gameUrl;
        } catch (Exception e) {
            logger.warning(e.toString());
            throw new GameServiceUnavailableException("Game service is currently unavailable.");
        }
    }

    public void deleteLobby(UUID gameId){
        lobbyRepository.deleteLobbyByGameId(gameId);
    }

    @Transactional(readOnly = true)
    public List<Lobby> findAllOpenLobbies(String keycloakId) {
        usersApi.findIdByKeycloakId(keycloakId);
        return lobbyRepository.findByStatus(LobbyStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public List<Lobby> findOpenLobbiesForGame(UUID id, String keycloakId) {

        var gameId = gamesApi.findGameById(id).getId();
        usersApi.findIdByKeycloakId(keycloakId);

        return lobbyRepository.findByStatusAndGameId(LobbyStatus.OPEN, gameId);
    }

    public void leaveLobby(UUID id, String keycloakId) {

        var playerId = usersApi.findIdByKeycloakId(keycloakId);
        var lobbyId = new LobbyId(id);

        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        boolean readyToDelete = lobby.leave(playerId);
        if(readyToDelete){
            lobbyRepository.deleteById(lobby.getId());
        }
        else{
            lobbyRepository.save(lobby);
        }
    }
}

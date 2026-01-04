package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto;
import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto.PlatformPlayerDto;
import be.kdg.ipj3.lobbies.api.dto.LobbyDetailDto;
import be.kdg.ipj3.lobbies.api.dto.PlayerDto;
import be.kdg.ipj3.lobbies.domain.Lobby;
import be.kdg.ipj3.lobbies.shared.LobbyId;
import be.kdg.ipj3.lobbies.domain.LobbyRepository;
import be.kdg.ipj3.lobbies.domain.LobbyStatus;
import be.kdg.ipj3.social.shared.ChatsApi;
import be.kdg.ipj3.users.shared.ServiceUnavailableException;
import be.kdg.ipj3.lobbies.infrastructure.rest.GameClient;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.lobbies.shared.LobbyApi;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.sessions.shared.SessionsApi;
import be.kdg.ipj3.shared.domain.exceptions.UserNotFoundException;
import be.kdg.ipj3.users.shared.ServiceUnavailableException;
import be.kdg.ipj3.users.shared.SmallUserDto;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Logger;

@Service
@Transactional
public class LobbyService implements LobbyApi {
    private final GameClient gameClient;
    private final LobbyRepository lobbyRepository;
    private final GamesApi gamesApi;
    private final UsersApi usersApi;
    private final SessionsApi sessionsApi;
    private final ChatsApi chatsApi;
    private final Logger logger = Logger.getLogger(LobbyService.class.getName());

    public LobbyService(GameClient gameClient, LobbyRepository lobbyRepository, GamesApi gamesApi, UsersApi usersApi,
                        SessionsApi sessionsApi, ChatsApi chatsApi) {
        this.gameClient = gameClient;
        this.lobbyRepository = lobbyRepository;
        this.gamesApi = gamesApi;
        this.usersApi = usersApi;
        this.sessionsApi = sessionsApi;
        this.chatsApi = chatsApi;
    }

    public Lobby createLobby(String keycloakId, String password, UUID gameIdParam) {
        var hostId = usersApi.findIdByKeycloakId(keycloakId);
        if (hostId == null) {
            throw new UserNotFoundException(keycloakId);
        }

        var game = gamesApi.findGameById(gameIdParam);

        Lobby lobby = new Lobby(password, game.getMaxPlayers(), game.getId(), hostId);

        lobbyRepository.save(lobby);

        return lobby;
    }

    public void joinLobby(UUID id, String keycloakId, String password) {

        var playerId = usersApi.findIdByKeycloakId(keycloakId);
        if (playerId == null) {
            throw new UserNotFoundException(keycloakId);
        }

        var lobbyId = new LobbyId(id);
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        lobby.join(playerId, password);

        lobbyRepository.save(lobby);
    }

    public void addBot(UUID id, String keycloakId, Difficulty difficulty) {
        var playerId = usersApi.findIdByKeycloakId(keycloakId);
        if (playerId == null) {
            throw new UserNotFoundException(keycloakId);
        }

        var lobbyId = new LobbyId(id);
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        if (lobby.getHost().equals(playerId)) {
            lobby.addBot(difficulty);
        }

        lobbyRepository.save(lobby);
    }

    public void removeBot(UUID id, String keycloakId, int botNumber) {
        var playerId = usersApi.findIdByKeycloakId(keycloakId);
        if (playerId == null) {
            throw new UserNotFoundException(keycloakId);
        }

        var lobbyId = new LobbyId(id);
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        if (lobby.getHost().equals(playerId)) {
            lobby.removeBot(botNumber);
        }

        lobbyRepository.save(lobby);
    }

    public Lobby findLobbyById(UUID id) {
        var lobbyId = new LobbyId(id);
        return lobbyRepository.findById(lobbyId).orElseThrow(lobbyId::notFound);
    }


    public LobbyDetailDto getLobbyDetails(UUID lobbyId, String keycloakId) {
        var id = usersApi.findIdByKeycloakId(keycloakId);
        if (id == null) {
            throw new UserNotFoundException(keycloakId);
        }
        Lobby lobby = findLobbyById(lobbyId);

        return getLobbyDetailDto(lobby);
    }

    public void resetLobbyTimer(UUID activeGameId) {
        var lobby = lobbyRepository.findByGameId(activeGameId);
        if (lobby.isEmpty()) {
            logger.warning("Lobby with active game id " + activeGameId + " is empty");
        } else {
            var presentLobby = lobby.get();
            presentLobby.resetTimer();
            lobbyRepository.save(presentLobby);
        }
    }

    public String startGame(UUID id, String requestorKeycloakId) {
        var requestor = usersApi.findIdByKeycloakId(requestorKeycloakId);
        if (requestor == null) {
            throw new UserNotFoundException(requestorKeycloakId);
        }

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
        Difficulty difficulty = Difficulty.EASY;

        if (!lobby.getBots().isEmpty()) {
            difficulty = lobby.getBots().getFirst();
        }

        SessionId sessionId = SessionId.create();
        GameStartRequestDto dto = new GameStartRequestDto(sessionId.id(), players, difficulty);

        try {
            if (gamesApi.isExternalGame(gameId)) {
                serviceUrl = serviceUrl.replace("{gameId}", sessionId.id().toString());

                UUID whiteId = null;
                String whiteName = null;
                UUID blackId = null;
                String blackName = null;

                if (!players.isEmpty()) {
                    whiteId = players.getFirst().id();
                    whiteName = players.getFirst().username();
                }
                if (players.size() >= 2) {
                    blackId = players.get(1).id();
                    blackName = players.get(1).username();
                }

                dto = new GameStartRequestDto(
                        sessionId.id(),
                        players,
                        whiteId,
                        whiteName,
                        blackId,
                        blackName,
                        difficulty
                );
            }

            String frontendUrl = gamesApi.findFrontendUrlByGameId(gameId);

            String gameUrl = frontendUrl + "/" + sessionId.id();

            gameClient.startGame(serviceUrl, dto);

            List<UUID> playerList = lobby.getPlayers().stream()
                    .map(UserId::id)
                    .toList();
            sessionsApi.createSession(sessionId.id(), gameId, playerList, difficulty);

            lobby.startGame(gameUrl);
            lobbyRepository.save(lobby);

            return gameUrl;
        } catch (Exception e) {
            logger.warning(e.toString());
            throw new ServiceUnavailableException("Game service is currently unavailable.");
        }
    }

    public void deleteLobby(UUID lobbyGameId) {
        lobbyRepository.deleteLobbyByGameId(lobbyGameId);
    }

    @Transactional(readOnly = true)
    public List<Lobby> findAllOpenLobbies(String keycloakId) {
        var userId = usersApi.findIdByKeycloakId(keycloakId);
        if (userId == null) {
            throw new UserNotFoundException(keycloakId);
        }
        return lobbyRepository.findByStatus(LobbyStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public List<Lobby> findOpenLobbiesForGame(UUID id, String keycloakId) {

        var gameId = gamesApi.findGameById(id).getId();
        var user = usersApi.findIdByKeycloakId(keycloakId);
        if (user == null) {
            throw new UserNotFoundException(keycloakId);
        }

        return lobbyRepository.findByStatusAndGameId(LobbyStatus.OPEN, gameId);
    }

    public void leaveLobby(UUID id, String keycloakId) {

        var playerId = usersApi.findIdByKeycloakId(keycloakId);
        if (playerId == null) {
            throw new UserNotFoundException(keycloakId);
        }
        var lobbyId = new LobbyId(id);

        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        boolean readyToDelete = lobby.leave(playerId);
        if (readyToDelete) {
            lobbyRepository.deleteById(lobby.getId());
        } else {
            lobbyRepository.save(lobby);
        }
    }

    public void invite(UUID lobbyUUID, String keycloakId, UUID receiverUUID) {
        var senderId = usersApi.findIdByKeycloakId(keycloakId);
        if(senderId == null){
            throw new UserNotFoundException(keycloakId);
        }

        UserId receiverId = new UserId(receiverUUID);
        if (!usersApi.existsById(receiverUUID)) {
            throw receiverId.notFound();
        }

        var lobbyId = new LobbyId(lobbyUUID);

        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        var game = gamesApi.findGameById(lobby.getGame().id());
        chatsApi.sendInvite(keycloakId, game.getName(), lobby.getPassword(), lobbyId, senderId, receiverId);
    }

    public LobbyDetailDto findCurrentLobbyDetailDtoForPlayer(String keycloakId) {
        var userId = usersApi.findIdByKeycloakId(keycloakId);
        if (userId == null) {
            throw new UserNotFoundException(keycloakId);
        }
        Lobby lobby = lobbyRepository.findCurrentLobbyByUserId(userId)
                .orElse(null);

        if (lobby == null) {
            return null;
        }

        return getLobbyDetailDto(lobby);
    }

    public String findGameName(GameId gameId) {
        return gamesApi.findGameById(gameId.id()).getName();
    }

    private LobbyDetailDto getLobbyDetailDto(Lobby lobby) {
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
}

package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto;
import be.kdg.ipj3.lobbies.domain.exception.GameServiceUnavailableException;
import be.kdg.ipj3.lobbies.domain.lobby.*;
import be.kdg.ipj3.lobbies.infrastructure.rest.GameClient;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LobbyService {
    private final GameClient gameClient;
    private final ILobbyRepository lobbyRepository;

    public LobbyService(GameClient gameClient, ILobbyRepository lobbyRepository) {
        this.gameClient = gameClient;
        this.lobbyRepository = lobbyRepository;
    }


    public Lobby createLobby(String password, int maxPlayers, GameId game, UserId host) {
        Lobby lobby = new Lobby(password, maxPlayers, game, host);

        lobbyRepository.save(lobby);

        return lobby;
    }

    public void joinLobby(LobbyId lobbyId, UserId player, String password) {

        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        lobby.join(player, password);

        lobbyRepository.save(lobby);
    }

    public String startGame(String serviceUrl, LobbyId lobbyId, UserId requestor, Difficulty difficulty) {
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

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


    @Transactional(readOnly = true)
    public List<Lobby> findAllOpenLobbies() {
        return lobbyRepository.findByStatus(LobbyStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public List<Lobby> findOpenLobbiesForGame(GameId gameId) {
        return lobbyRepository.findByStatusAndGameId(LobbyStatus.OPEN, gameId);
    }

    public void leaveLobby(LobbyId lobbyId, UserId playerId) {
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(lobbyId::notFound);

        lobby.leave(playerId);
        lobbyRepository.save(lobby);
    }
}

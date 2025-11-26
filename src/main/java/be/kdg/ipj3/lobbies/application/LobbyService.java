package be.kdg.ipj3.lobbies.application;

import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto;
import be.kdg.ipj3.lobbies.domain.exception.GameServiceUnavailableException;
import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.lobbies.infrastructure.rest.GameClient;
import be.kdg.ipj3.shared.domain.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LobbyService {
    private final GameClient gameClient;

    public LobbyService(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public String startGame(String serviceUrl, List<UserId> players, Difficulty difficulty) {
        try {
            List<UUID> playerIds = players.stream()
                    .map(UserId::id)
                    .toList();

            GameStartRequestDto dto = new GameStartRequestDto(playerIds, difficulty);

            return gameClient.startGame(serviceUrl, dto).gameUrl();
        } catch (Exception e) {
            throw new GameServiceUnavailableException("Game service is currently unavailable.");
        }
    }
}

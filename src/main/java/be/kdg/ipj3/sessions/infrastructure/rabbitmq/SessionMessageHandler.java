package be.kdg.ipj3.sessions.infrastructure.rabbitmq;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.shared.LobbyApi;
import be.kdg.ipj3.sessions.application.SessionService;
import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.infrastructure.rabbitmq.config.RabbitMQTopologySessions;
import be.kdg.ipj3.sessions.infrastructure.rabbitmq.dto.GameRematchDto;
import be.kdg.ipj3.sessions.infrastructure.rabbitmq.dto.SessionStartDto;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.sessions.shared.SessionStatus;
import be.kdg.ipj3.shared.domain.exceptions.UserNotFoundException;
import be.kdg.ipj3.shared.infrastructure.rabbitmq.dto.GameResultMessageDto;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionMessageHandler {

    private final SessionService sessionService;
    private final UsersApi usersApi;
    private final LobbyApi lobbyApi;

    public SessionMessageHandler(SessionService sessionService, UsersApi usersApi, LobbyApi lobbyApi) {
        this.sessionService = sessionService;
        this.usersApi = usersApi;
        this.lobbyApi = lobbyApi;
    }

    @RabbitListener(queues = RabbitMQTopologySessions.SESSION_GAME_RESULTS_QUEUE)
    void onGameResultMessageReceived(GameResultMessageDto dto) {
        UserId winnerId = dto.winnerId() != null
                ? usersApi.findIdByKeycloakId(dto.winnerId()).orElseThrow(() -> new UserNotFoundException(dto.winnerId()))
                : null;

        sessionService.endSession(
                new SessionId(dto.gameId()),
                SessionStatus.fromStringOrNull(dto.status()),
                winnerId,
                dto.completedAt()
        );

        if(dto.endOfGame()){
            lobbyApi.deleteLobby(dto.gameId());
        }
        else{
            lobbyApi.resetLobbyTimer(dto.gameId());
        }
    }

    @RabbitListener(queues = RabbitMQTopologySessions.SESSION_GAME_REMATCH_QUEUE)
    void onGameRematchMessageReceived(GameRematchDto dto) {
        sessionService.createRematchSession(dto.oldGameId(), dto.newGameId());
        lobbyApi.resetLobbyTimer(dto.newGameId());
    }

    @RabbitListener(queues = RabbitMQTopologySessions.SESSION_GAME_START_QUEUE)
    void onGameStartMessageReceived(SessionStartDto sessionStartDto){
        List<UserId> playerIds = sessionStartDto.playerIds().stream()
                .map(UserId::new).toList();
        var session = new Session(new SessionId(sessionStartDto.sessionId()), new GameId(sessionStartDto.gameId()),playerIds,null);
        sessionService.saveSession(session);
    }
}

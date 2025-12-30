package be.kdg.ipj3.sessions.infrastructure.rabbitmq;

import be.kdg.ipj3.lobbies.shared.LobbyApi;
import be.kdg.ipj3.sessions.application.SessionService;
import be.kdg.ipj3.sessions.infrastructure.rabbitmq.config.RabbitMQTopologySessions;
import be.kdg.ipj3.sessions.infrastructure.rabbitmq.dto.GameRematchDto;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.sessions.shared.SessionStatus;
import be.kdg.ipj3.shared.infrastructure.rabbitmq.dto.GameResultMessageDto;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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
                ? usersApi.findIdByKeycloakId(dto.winnerId())
                : null;

        sessionService.endSession(
                new SessionId(dto.gameId()),
                SessionStatus.fromStringOrNull(dto.status()),
                winnerId,
                dto.completedAt()
        );

        lobbyApi.deleteLobby(dto.gameId());
    }

    @RabbitListener(queues = RabbitMQTopologySessions.SESSION_GAME_REMATCH_QUEUE)
    void onGameRematchMessageReceived(GameRematchDto dto) {
        sessionService.createRematchSession(dto.oldGameId(), dto.newGameId());
    }
}

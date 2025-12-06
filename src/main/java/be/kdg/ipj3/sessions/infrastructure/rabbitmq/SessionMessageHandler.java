package be.kdg.ipj3.sessions.infrastructure.rabbitmq;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.sessions.application.SessionService;
import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.infrastructure.rabbitmq.config.RabbitMQTopologySessions;
import be.kdg.ipj3.shared.infrastructure.rabbitmq.dto.GameResultMessageDto;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class SessionMessageHandler {

    private final SessionService sessionService;

    public SessionMessageHandler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @RabbitListener(queues = RabbitMQTopologySessions.SESSION_GAME_RESULTS_QUEUE)
    void onGameResultMessageReceived(GameResultMessageDto dto) {
        List<UserId> players = new ArrayList<>();
        for (UUID player : dto.players()) {
            players.add(new UserId(player));
        }

        Session session = new Session(
                new GameId(dto.gameId()),
                players,
                dto.difficulty(),
                dto.winnerId() != null ? new UserId(dto.winnerId()) : null,
                dto.sessionResult()
        );

        sessionService.save(session);
    }
}
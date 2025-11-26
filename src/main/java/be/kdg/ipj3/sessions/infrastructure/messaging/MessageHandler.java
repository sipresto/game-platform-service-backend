package be.kdg.ipj3.sessions.infrastructure.messaging;

import be.kdg.ipj3.sessions.application.SessionService;
import be.kdg.ipj3.sessions.infrastructure.messaging.dto.GameResultMessageDto;
import be.kdg.ipj3.shared.infrastructure.messaging.config.RabbitMQTopology;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageHandler {

    private final RabbitTemplate rabbitTemplate;
    private final SessionService sessionService;

    public MessageHandler(RabbitTemplate rabbitTemplate, SessionService sessionService) {
        this.rabbitTemplate = rabbitTemplate;
        this.sessionService = sessionService;
    }

    @RabbitListener(queues = RabbitMQTopology.GAME_RESULTS_QUEUE)
    void onUpdateFoodOrderMessageReceived(GameResultMessageDto gameResultMessageDto) {
        sessionService.save(gameResultMessageDto.toDomain());
    }
}

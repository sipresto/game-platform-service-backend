package be.kdg.ipj3.catalog.infrastructure.rabbitmq;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.infrastructure.rabbitmq.config.RabbitMQTopologyRegistrations;
import be.kdg.ipj3.catalog.infrastructure.rabbitmq.dto.RegisterGameDto;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMessageHandler {

    private final GameService gameService;

    public RegistrationMessageHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @RabbitListener(queues = RabbitMQTopologyRegistrations.GAME_REGISTRATION_QUEUE)
    void onGameRegistrationMessageReceived(@Valid RegisterGameDto dto) {
        gameService.registerGame(dto);
    }
}

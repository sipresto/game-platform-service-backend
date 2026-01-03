package be.kdg.ipj3.catalog.infrastructure.rabbitmq;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.infrastructure.rabbitmq.config.RabbitMQTopologyRegistrations;
import be.kdg.ipj3.catalog.infrastructure.rabbitmq.dto.RegisterGameDto;
import be.kdg.ipj3.chatbot.shared.ChatbotApi;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMessageHandler {

    private final GameService gameService;
    private final ChatbotApi chatbotApi;

    public RegistrationMessageHandler(GameService gameService, ChatbotApi chatbotApi) {
        this.gameService = gameService;
        this.chatbotApi = chatbotApi;
    }

    @RabbitListener(queues = RabbitMQTopologyRegistrations.GAME_REGISTRATION_QUEUE)
    void onGameRegistrationMessageReceived(@Valid RegisterGameDto dto) {
        gameService.registerGame(dto);
        if (dto.rules() != null && !dto.rules().isBlank()) {
            chatbotApi.indexGameRules(
                    dto.id(),
                    dto.name(),
                    dto.rules()
            );
        }
    }
}

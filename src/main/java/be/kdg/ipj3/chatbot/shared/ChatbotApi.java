package be.kdg.ipj3.chatbot.shared;

import org.springframework.modulith.NamedInterface;

import java.util.UUID;

@NamedInterface
public interface ChatbotApi {

    void indexGameRules(UUID gameId, String gameName, String rulesText);

}


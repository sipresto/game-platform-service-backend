package be.kdg.ipj3.chatbot.api;

import be.kdg.ipj3.chatbot.api.dto.ChatbotRequestDto;
import be.kdg.ipj3.chatbot.application.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/platform/ai")
public class ChatbotController {
    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chatWithAi(@RequestBody ChatbotRequestDto request) {
        String response = chatbotService.askAi(request.message());
        return ResponseEntity.ok(response);
    }
}
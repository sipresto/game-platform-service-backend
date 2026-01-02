package be.kdg.ipj3.chatbot.api;

import be.kdg.ipj3.chatbot.api.dto.ChatbotRequestDto;
import be.kdg.ipj3.chatbot.api.dto.ChatbotUploadResponseDto;
import be.kdg.ipj3.chatbot.application.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatbotUploadResponseDto> uploadFile(@RequestParam("file") MultipartFile file) {
        ChatbotUploadResponseDto response = chatbotService.uploadDocument(file);
        return ResponseEntity.ok(response);
    }
}
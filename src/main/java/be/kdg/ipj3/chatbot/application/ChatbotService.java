package be.kdg.ipj3.chatbot.application;

import be.kdg.ipj3.chatbot.api.dto.ChatbotRequestDto;
import be.kdg.ipj3.chatbot.api.dto.ChatbotUploadResponseDto;
import be.kdg.ipj3.chatbot.shared.ChatbotApi;
import be.kdg.ipj3.users.shared.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.core.io.Resource;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Service
public class ChatbotService implements ChatbotApi {
    private static final Logger log = LoggerFactory.getLogger(ChatbotService.class);
    private final RestClient restClient;

    public ChatbotService(RestClient.Builder builder, @Value("${rag.api.url}") String ragApiUrl) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(60));

        this.restClient = builder
                .baseUrl(ragApiUrl)
                .requestFactory(requestFactory)
                .build();
    }


    public void indexGameRules(UUID gameId, String gameName, String rulesText) {
        if (rulesText == null || rulesText.isBlank()) {
            log.warn("No rules provided for game: {}", gameName);
            return;
        }

        Resource rulesResource =
                new ByteArrayResource(rulesText.getBytes(StandardCharsets.UTF_8)) {
                    @Override
                    public String getFilename() {
                        return gameName.replaceAll("\\s+", "_") + "_Rules.txt";
                    }
                };

        try {
            uploadToRag(rulesResource);
            log.info("Successfully indexed rules for {}", gameName);
        } catch (ServiceUnavailableException e) {
            log.warn("RAG API is down or unreachable. Could not index rules for {}. (Error: {})", gameName, e.getMessage());        }
    }

    private void uploadToRag(Resource fileResource) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        restClient.post()
                .uri("/api/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(ChatbotUploadResponseDto.class);
    }

    public String askAi(String userMessage) {
        ChatbotRequestDto request = new ChatbotRequestDto(userMessage);

        return restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }
}
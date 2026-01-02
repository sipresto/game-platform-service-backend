package be.kdg.ipj3.chatbot.application;

import be.kdg.ipj3.chatbot.api.dto.ChatbotRequestDto;
import be.kdg.ipj3.chatbot.api.dto.ChatbotUploadResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.net.http.HttpClient;
import java.time.Duration;

@Service
public class ChatbotService {
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

    public String askAi(String userMessage) {
        ChatbotRequestDto request = new ChatbotRequestDto(userMessage);

        return restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }

    public ChatbotUploadResponseDto uploadDocument(MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        return restClient.post()
                .uri("/api/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(ChatbotUploadResponseDto.class);
    }
}
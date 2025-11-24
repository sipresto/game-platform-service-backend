package be.kdg.ipj3.sessions.infrastructure.rest;

import be.kdg.ipj3.sessions.api.dto.GameCreatedResponseDto;
import be.kdg.ipj3.sessions.api.dto.GameStartRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GameClient {
    private final RestClient restClient;

    public GameClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public GameCreatedResponseDto startGame(String serviceUrl, GameStartRequestDto request) {
        return restClient
                .post()
                .uri(serviceUrl)
                .body(request)
                .retrieve()
                .body(GameCreatedResponseDto.class);
    }
}

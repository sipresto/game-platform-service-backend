package be.kdg.ipj3.lobbies.infrastructure.rest;

import be.kdg.ipj3.lobbies.api.dto.GameStartRequestDto;
import be.kdg.ipj3.shared.infrastructure.security.ServiceClientCredentials;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GameClient {
    private final RestClient restClient;
    private final ServiceClientCredentials serviceClientCredentials;

    public GameClient(RestClient restClient, ServiceClientCredentials serviceClientCredentials) {
        this.restClient = restClient;
        this.serviceClientCredentials = serviceClientCredentials;
    }

    public void startGame(String serviceUrl, GameStartRequestDto request) {
        String token = serviceClientCredentials.getAccessTokenForService();
        restClient
                .post()
                .uri(serviceUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
package be.kdg.ipj3.sessions.application;

import be.kdg.ipj3.sessions.infrastructure.rest.GameClient;
import org.springframework.stereotype.Service;

@Service
public class LobbyService {
    private final GameClient gameClient;

    public LobbyService(GameClient gameClient) {
        this.gameClient = gameClient;
    }


}

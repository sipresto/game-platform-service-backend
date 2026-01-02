package be.kdg.ipj3.lobbies.shared;

import org.springframework.modulith.NamedInterface;

import java.util.UUID;

@NamedInterface
public interface LobbyApi {
    void deleteLobby(UUID lobbyGameId);
    void resetLobbyTimer(UUID activeGameId);
}

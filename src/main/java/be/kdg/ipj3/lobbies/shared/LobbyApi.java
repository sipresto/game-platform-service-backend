package be.kdg.ipj3.lobbies.shared;

import java.util.UUID;

public interface LobbyApi {
    void deleteLobby(UUID lobbyGameId);
    void resetLobbyTimer(UUID activeGameId);
}

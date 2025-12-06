package be.kdg.ipj3.lobbies.api.dto;

import be.kdg.ipj3.lobbies.domain.Lobby;

import java.util.UUID;

public record LobbyListDto(
        UUID id,
        int currentPlayerCount,
        boolean isPrivate
) {
    public static LobbyListDto fromDomain(Lobby lobby) {
        return new LobbyListDto(
                lobby.getId().id(),
                lobby.getPlayers().size(),
                lobby.isPrivate()
        );
    }
}

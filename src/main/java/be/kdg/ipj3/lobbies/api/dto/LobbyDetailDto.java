package be.kdg.ipj3.lobbies.api.dto;

import be.kdg.ipj3.lobbies.domain.Lobby;

import java.util.List;
import java.util.UUID;

public record LobbyDetailDto(
        UUID id,
        UUID gameId,
        String hostId,
        int currentPlayerCount,
        int maxPlayers,
        boolean isPrivate,
        boolean started,
        String gameUrl,
        List<PlayerDto> players
) {
    public static LobbyDetailDto fromDomain(Lobby lobby, List<PlayerDto> players) {
        return new LobbyDetailDto(
                lobby.getId().id(),
                lobby.getGame().id(),
                lobby.getHost().id().toString(),
                lobby.getPlayers().size(),
                lobby.getMaxPlayers(),
                lobby.isPrivate(),
                lobby.getActiveGameUrl() != null && !lobby.getActiveGameUrl().isBlank(),
                lobby.getActiveGameUrl(),
                players
        );
    }
}
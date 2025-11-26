package be.kdg.ipj3.lobbies.api.dto;

import java.util.UUID;

public record LobbyListDto(UUID id,
                           int currentPlayerCount,
                           boolean isPrivate
) {
}
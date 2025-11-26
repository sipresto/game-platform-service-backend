package be.kdg.ipj3.lobbies.api.dto;

import java.util.UUID;

public record CreateLobbyDto(
                             String password,
                             int maxPlayers,
                             UUID gameId
) {
}
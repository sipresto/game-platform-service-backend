package be.kdg.ipj3.lobbies.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateLobbyDto(
        String password,
        @NotNull UUID gameId
) {
}

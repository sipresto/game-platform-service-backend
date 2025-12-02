package be.kdg.ipj3.lobbies.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateLobbyDto(
        String password,
        @Min(1) @Max(20) int maxPlayers,
        @NotNull UUID gameId
) {
}
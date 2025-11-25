package be.kdg.ipj3.lobbies.api.dto;

import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record GameStartRequestDto(
        @Size(min = 1, max = 2) List<UUID> players,
        @NotNull Difficulty difficulty
) {
}

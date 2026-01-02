package be.kdg.ipj3.lobbies.api.dto;

import be.kdg.ipj3.lobbies.shared.Difficulty;
import jakarta.validation.constraints.NotNull;

public record AddBotDto(
        @NotNull Difficulty difficulty
) {
}

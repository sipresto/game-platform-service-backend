package be.kdg.ipj3.lobbies.api.dto;

import jakarta.validation.constraints.NotNull;

public record RemoveBotDto(
        @NotNull int botNumber
) {
}

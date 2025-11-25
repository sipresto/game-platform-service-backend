package be.kdg.ipj3.lobbies.api.dto;

import jakarta.validation.constraints.NotBlank;

public record GameCreatedResponseDto(
        @NotBlank String gameUrl
) {
}

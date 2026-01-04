package be.kdg.ipj3.lobbies.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InvitePlayerDto(
        @NotNull UUID userId
        ) {
}

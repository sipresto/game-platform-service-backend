package be.kdg.ipj3.lobbies.api.dto;

import be.kdg.ipj3.lobbies.shared.Difficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GameStartRequestDto(
        @Valid @Size(min = 1) List<PlatformPlayerDto> players,
        Difficulty difficulty
) {
}


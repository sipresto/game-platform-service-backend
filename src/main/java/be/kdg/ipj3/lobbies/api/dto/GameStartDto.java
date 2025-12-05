package be.kdg.ipj3.lobbies.api.dto;

import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// TODO: Remove players and gameId, as they are stored in the lobby object and difficulty should be nullable
public record GameStartDto (
        @NotNull UUID gameId,
        @NotNull Difficulty difficulty
) {
}

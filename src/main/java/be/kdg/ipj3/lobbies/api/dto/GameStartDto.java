package be.kdg.ipj3.lobbies.api.dto;

import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

// TODO: Remove players and gameId, as they are stored in the lobby object and difficulty should be nullable
public record GameStartDto (
        @NotNull UUID gameId,
        @Size(min = 1, max = 2) List<UUID> players,
        @NotNull Difficulty difficulty
) {
}

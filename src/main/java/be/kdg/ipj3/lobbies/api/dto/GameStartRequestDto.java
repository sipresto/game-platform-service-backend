package be.kdg.ipj3.lobbies.api.dto;

import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.users.shared.SmallUserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record GameStartRequestDto(
        @NotNull UUID gameId,
        @Valid @Size(min = 1) List<PlatformPlayerDto> players,
        UUID whitePlayerId,
        String whitePlayerName,
        UUID blackPlayerId,
        String blackPlayerName,

        Difficulty difficulty
) {

    public GameStartRequestDto(UUID gameId, List<PlatformPlayerDto> players, Difficulty difficulty) {
        this(gameId, players, null, null, null, null, difficulty);
    }

    public record PlatformPlayerDto(
            UUID id,
            String username,
            String keycloakId,
            String displayName
    ) {
        public static PlatformPlayerDto fromDto(SmallUserDto dto) {
            return new PlatformPlayerDto(
                    dto.id(),
                    dto.username(),
                    dto.keycloakId(),
                    dto.displayName()
            );
        }
    }
}
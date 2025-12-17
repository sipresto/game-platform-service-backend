package be.kdg.ipj3.lobbies.api.dto;

import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.users.shared.SmallUserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record GameStartRequestDto(
        @NotNull UUID gameId,
        @Valid @Size(min = 1) List<PlatformPlayerDto> players,
        Difficulty difficulty
) {
    public record PlatformPlayerDto(
            @NotBlank String keycloakId,
            @NotNull String displayName
    ) {
        public static PlatformPlayerDto fromDto(SmallUserDto smallUserDto) {
            return new PlatformPlayerDto(smallUserDto.keycloakId(), smallUserDto.displayName());
        }
    }
}

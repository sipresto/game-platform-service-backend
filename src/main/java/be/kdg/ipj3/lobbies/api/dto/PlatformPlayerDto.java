package be.kdg.ipj3.lobbies.api.dto;

import be.kdg.ipj3.users.shared.SmallUserDto;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PlatformPlayerDto(
        @NotNull UUID id,
        @NotNull String name
) {
    public static PlatformPlayerDto fromDto(SmallUserDto smallUserDto) {
        return new PlatformPlayerDto(smallUserDto.id().id(),  smallUserDto.name());
    }
}

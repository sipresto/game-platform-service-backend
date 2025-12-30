package be.kdg.ipj3.users.api.dto;

import be.kdg.ipj3.users.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BlockedUserDto(
        @NotNull UUID id,
        @NotBlank String username,
        @NotNull UserDto.ProfileDto profile
        ) {
    public static BlockedUserDto fromDomain(User user) {
        return new BlockedUserDto(
                user.getId().id(),
                user.getUsername(),
                UserDto.ProfileDto.fromDomain(user.getProfile())
        );
    }
}

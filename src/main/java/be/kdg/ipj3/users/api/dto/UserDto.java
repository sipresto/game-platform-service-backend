package be.kdg.ipj3.users.api.dto;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.api.SmallGameDto;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UserDto(
        @NotNull UUID id,
        @NotBlank String keycloakId,
        @NotBlank String username,
        @Valid List<SmallGameDto> games,
        @Valid @NotNull ProfileDto profile
) {
    public static UserDto fromDomain(User user) {
        return new UserDto(
                user.getId().id(),
                user.getKeycloakId(),
                user.getUsername(),
                List.of(),
                ProfileDto.fromDomain(user.getProfile())
        );
    }

    public static UserDto fromDomainWithGames(User user, List<Game> games) {
        return new UserDto(
                user.getId().id(),
                user.getKeycloakId(),
                user.getUsername(),
                games.stream().map(SmallGameDto::fromDomain).toList(),
                ProfileDto.fromDomain(user.getProfile())
        );
    }

    public record ProfileDto(
            @NotBlank String displayName,
            String bio
    ) {
        public static ProfileDto fromDomain(Profile profile) {
            return new ProfileDto(
                    profile.displayName(),
                    profile.bio()
            );
        }
    }
}

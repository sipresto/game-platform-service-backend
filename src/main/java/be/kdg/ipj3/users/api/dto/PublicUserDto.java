package be.kdg.ipj3.users.api.dto;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.shared.UserId;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public record PublicUserDto(
        @NotNull UUID id,
        @NotBlank String username,
        @NotNull ProfileDto profile,
        @JsonInclude(NON_NULL)
        Integer points,
        @JsonInclude(NON_NULL)
        List<UUID> favoriteGames,
        @JsonInclude(NON_NULL)
        List<UUID> ownedGames,
        @JsonInclude(NON_NULL)
        List<UUID> friends,
        String keycloakId
) {
    public static PublicUserDto fromDomain(User user, User viewer) {
        Profile visibleProfile = user.getVisibleProfile(viewer);
        Integer visiblePoints = user.getVisiblePoints(viewer);
        List<GameId> visibleFavoriteGames = user.getVisibleFavoriteGames(viewer);
        List<GameId> visibleOwnedGames = user.getVisibleOwnedGames(viewer);
        List<UserId> visibleFriends = user.getVisibleFriends(viewer);

        return new PublicUserDto(
                user.getId().id(),
                user.getUsername(),
                ProfileDto.fromDomain(visibleProfile),
                visiblePoints,
                visibleFavoriteGames != null ?
                        visibleFavoriteGames.stream().map(GameId::id).toList() : null,
                visibleOwnedGames != null ?
                        visibleOwnedGames.stream().map(GameId::id).toList() : null,
                visibleFriends != null ?
                        visibleFriends.stream().map(UserId::id).toList() : null,
                user.getKeycloakId()
        );
    }

    public record ProfileDto(
            @NotBlank String displayName,
            @JsonInclude(NON_NULL)
            String bio,
            @JsonInclude(NON_NULL)
            Boolean hasProfileImage
    ) {
        public static ProfileDto fromDomain(Profile profile) {
            return new ProfileDto(
                    profile.displayName(),
                    profile.bio(),
                    profile.getHasProfileImage()
            );
        }
    }
}
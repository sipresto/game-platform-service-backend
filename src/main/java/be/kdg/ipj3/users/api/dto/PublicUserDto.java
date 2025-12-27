package be.kdg.ipj3.users.api.dto;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.domain.*;
import be.kdg.ipj3.users.shared.UserId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record PublicUserDto(
        @NotNull UUID id,
        @NotBlank String username,
        @NotNull ProfileDto profile,
        @JsonInclude(Include.NON_ABSENT)
        Optional<Integer> points,
        @JsonInclude(Include.NON_ABSENT)
        Optional<List<UUID>> favoriteGames,
        @JsonInclude(Include.NON_ABSENT)
        Optional<List<UUID>> ownedGames,
        @JsonInclude(Include.NON_ABSENT)
        Optional<List<UUID>> friends,
        String keycloakId
) {
    public static PublicUserDto fromDomain(User user, User viewer) {
        Profile visibleProfile = user.getVisibleProfile(viewer);
        Optional<Integer> visiblePoints = user.getVisiblePoints(viewer);
        Optional<List<GameId>> visibleFavoriteGames = user.getVisibleFavoriteGames(viewer);
        Optional<List<GameId>> visibleOwnedGames = user.getVisibleOwnedGames(viewer);
        Optional<List<UserId>> visibleFriends = user.getVisibleFriends(viewer);

        return new PublicUserDto(
                user.getId().id(),
                user.getUsername(),
                ProfileDto.fromDomain(visibleProfile),
                visiblePoints,
                visibleFavoriteGames.map(games ->
                        games.stream().map(GameId::id).toList()),
                visibleOwnedGames.map(games ->
                        games.stream().map(GameId::id).toList()),
                visibleFriends.map(friends ->
                        friends.stream().map(UserId::id).toList()),
                user.getKeycloakId()
        );
    }

    public record ProfileDto(
            @NotBlank String displayName,
            @JsonInclude(Include.NON_ABSENT)
            Optional<String> bio,
            @JsonInclude(Include.NON_ABSENT)
            Optional<Boolean> hasProfileImage
    ) {
        public static ProfileDto fromDomain(Profile profile) {
            return new ProfileDto(
                    profile.displayName(),
                    profile.getBio(),
                    profile.getHasProfileImage()
            );
        }
    }
}
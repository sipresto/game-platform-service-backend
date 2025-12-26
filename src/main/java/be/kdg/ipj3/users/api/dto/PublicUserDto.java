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
        Optional<List<UUID>> friends
) {
    public static PublicUserDto fromDomain(User user, User viewer) {
        VisibilitySettings visibilitySettings = user.getVisibilitySettings();

        return new PublicUserDto(
                user.getId().id(),
                user.getUsername(),
                ProfileDto.fromDomain(user.getProfile(), visibilitySettings, viewer, user),
                getPointsIfVisible(user, visibilitySettings, viewer),
                getFavoriteGamesIfVisible(user, visibilitySettings, viewer),
                getOwnedGamesIfVisible(user, visibilitySettings, viewer),
                getFriendsIfVisible(user, visibilitySettings, viewer)
        );
    }

    private static Optional<Integer> getPointsIfVisible(User user, VisibilitySettings settings, User viewer) {
        if (viewer != null && viewer.getId().equals(user.getId())) {
            return Optional.of(user.getPoints());
        }

        if (settings.isVisibleTo(VisibleField.POINTS, viewer, user)) {
            return Optional.of(user.getPoints());
        }
        return Optional.empty();
    }

    private static Optional<List<UUID>> getFavoriteGamesIfVisible(User user, VisibilitySettings settings, User viewer) {
        if (viewer != null && viewer.getId().equals(user.getId())) {
            return Optional.of(user.getFavoriteGames().stream()
                    .map(GameId::id)
                    .toList());
        }

        if (settings.isVisibleTo(VisibleField.FAVORITE_GAMES, viewer, user)) {
            return Optional.of(user.getFavoriteGames().stream()
                    .map(GameId::id)
                    .toList());
        }
        return Optional.empty();
    }

    private static Optional<List<UUID>> getOwnedGamesIfVisible(User user, VisibilitySettings settings, User viewer) {
        if (viewer != null && viewer.getId().equals(user.getId())) {
            return Optional.of(user.getOwnedGames().stream()
                    .map(GameId::id)
                    .toList());
        }

        if (settings.isVisibleTo(VisibleField.OWNED_GAMES, viewer, user)) {
            return Optional.of(user.getOwnedGames().stream()
                    .map(GameId::id)
                    .toList());
        }
        return Optional.empty();
    }

    private static Optional<List<UUID>> getFriendsIfVisible(User user, VisibilitySettings settings, User viewer) {
        if (viewer != null && viewer.getId().equals(user.getId())) {
            return Optional.of(user.getFriends().stream()
                    .map(UserId::id)
                    .toList());
        }

        if (settings.isVisibleTo(VisibleField.FRIENDS_LIST, viewer, user)) {
            return Optional.of(user.getFriends().stream()
                    .map(UserId::id)
                    .toList());
        }
        return Optional.empty();
    }

    public record ProfileDto(
            @NotBlank String displayName,
            @JsonInclude(Include.NON_ABSENT)
            Optional<String> bio,
            @JsonInclude(Include.NON_ABSENT)
            Optional<Boolean> hasProfileImage
    ) {
        public static ProfileDto fromDomain(Profile profile, VisibilitySettings settings, User viewer, User target) {
            if (viewer != null && viewer.getId().equals(target.getId())) {
                return new ProfileDto(
                        profile.displayName(),
                        Optional.ofNullable(profile.bio()),
                        Optional.of(profile.profileImage() != null && profile.imageMimeType() != null)
                );
            }

            boolean profileVisible = settings.isVisibleTo(VisibleField.PROFILE, viewer, target);

            return new ProfileDto(
                    profile.displayName(),
                    profileVisible ? Optional.ofNullable(profile.bio()) : Optional.empty(),
                    profileVisible ? Optional.of(profile.profileImage() != null && profile.imageMimeType() != null) : Optional.empty()
            );
        }
    }
}
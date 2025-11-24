package be.kdg.ipj3.users.api;

import be.kdg.ipj3.users.domain.Profile;

public record ProfileDto(
        String userId,
        String displayName,
        String bio
) {
    public static ProfileDto from(Profile profile) {
        return new ProfileDto(
                profile.getOwner().value(),
                profile.getUsername(),
                profile.getBio()
        );
    }
}
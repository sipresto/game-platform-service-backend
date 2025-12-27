package be.kdg.ipj3.users.domain;

import java.util.Optional;

public record Profile(
        String displayName,
        String bio,
        byte[] profileImage,
        String imageMimeType
) {
    public Profile(String displayName, String bio) {
        this(displayName, bio, null, null);
    }

    public Profile(String displayName, String bio, byte[] profileImage, String imageMimeType) {
        this.displayName = displayName;
        this.bio = bio;
        this.profileImage = profileImage;
        this.imageMimeType = imageMimeType;
    }

    public boolean hasProfileImage() {
        return profileImage != null && imageMimeType != null;
    }

    public Optional<String> getBio() {
        return Optional.ofNullable(bio);
    }

    public Optional<Boolean> getHasProfileImage() {
        return Optional.of(hasProfileImage());
    }
}
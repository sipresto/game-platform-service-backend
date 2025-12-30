package be.kdg.ipj3.users.domain;

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

    public boolean getHasProfileImage() {
        return profileImage != null;
    }
}
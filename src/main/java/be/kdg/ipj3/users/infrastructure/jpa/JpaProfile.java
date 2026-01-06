package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.users.domain.Profile;
import jakarta.persistence.*;

@Embeddable
public class JpaProfile {
    @Column(nullable = false)
    private String displayName;

    @Column(length = 500)
    private String bio;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(name = "image_mime_type", length = 50)
    private String imageMimeType;

    protected JpaProfile() {
    } // for JPA

    public JpaProfile(String displayName, String bio, byte[] profileImage, String imageMimeType) {
        this.displayName = displayName;
        this.bio = bio;
        this.profileImage = profileImage;
        this.imageMimeType = imageMimeType;
    }

    public static JpaProfile fromDomain(Profile profile) {
        return new JpaProfile(
                profile.displayName(),
                profile.bio(),
                profile.profileImage(),
                profile.imageMimeType()
        );
    }

    public Profile toDomain() {
        return new Profile(displayName, bio, profileImage, imageMimeType);
    }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public byte[] getProfileImage() { return profileImage; }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }
    public String getImageMimeType() { return imageMimeType; }
    public void setImageMimeType(String imageMimeType) { this.imageMimeType = imageMimeType; }
}
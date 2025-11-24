package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.Profile;
import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class JpaProfileEntity {
    @Id
    private String userId;

    private String displayName;

    @Column(length = 1000)
    private String bio;

    protected JpaProfileEntity() {
    }

    public JpaProfileEntity(String userId, String displayName, String bio) {
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Profile toDomain() {
        return new Profile(new UserId(userId), displayName, bio);
    }

    public static JpaProfileEntity fromDomain(Profile profile) {
        return new JpaProfileEntity(profile.getOwner().value(), profile.getUsername(), profile.getBio());
    }
}
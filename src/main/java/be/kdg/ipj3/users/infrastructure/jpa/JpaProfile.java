package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.users.domain.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class JpaProfile {
    @Column(nullable=false)
    private String displayName;

    private String bio;

    protected JpaProfile() {
    } // for JPA

    public JpaProfile(String displayName, String bio) {
        this.displayName = displayName;
        this.bio = bio;
    }

    public static JpaProfile fromDomain(Profile profile) {
        return new JpaProfile(profile.displayName(), profile.bio());
    }

    public Profile toDomain() {
        return new Profile(displayName, bio);
    }
}
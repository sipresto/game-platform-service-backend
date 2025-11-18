package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class Profile {
    private final UserId owner;
    private final String username;
    private final String bio;

    public Profile(UserId owner, String username, String bio) {
        this.owner = owner;
        this.username = username;
        this.bio = bio;
    }

    public UserId getOwner() {
        return owner;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

}

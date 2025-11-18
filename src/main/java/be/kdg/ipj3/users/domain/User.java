package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
public class User {
    @Identity
    private final UserId id;
    private final String username;

    public User(UserId id, String username) {
        this.id = id;
        this.username = username;
    }

    public static User create(String username) {
        return new User(UserId.create(), username);
    }

    public UserId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}

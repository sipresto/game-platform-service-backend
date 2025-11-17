package be.kdg.ipj3.shared.domain.user;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.UUID;

@AggregateRoot
public class User {
    @Identity
    private final UserId userId;
    private UserName username;

    protected User(UserName username) {
        this.username = username;
        this.userId = new UserId(UUID.randomUUID());
    }

    protected User(UserId userId, UserName username) {
        this.userId = userId;
        this.username = username;
    }

    public UserId userId() {
        return userId;
    }

    public UserName username() {
        return username;
    }

    public void updateUsername(String username){
        this.username = new UserName(username);
    }
}

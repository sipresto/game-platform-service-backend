package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.List;

@AggregateRoot
public class User {
    @Identity
    private final UserId id;
    private final String username;
    private List<GameId> ownedGames;

    public User(UserId id, String username, List<GameId> ownedGames) {
        this.id = id;
        this.username = username;
        this.ownedGames = ownedGames;
    }

    public static User create(String username) {
        return new User(UserId.create(), username, List.of());
    }

    public UserId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public List<GameId> getOwnedGames() {
        return ownedGames;
    }
}

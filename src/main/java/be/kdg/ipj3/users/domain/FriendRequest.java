package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.ValueObject;

import java.time.Instant;

@ValueObject
public record FriendRequest(UserId sender, Instant createdAt) {
    public static FriendRequest create(UserId sender) {
        return new FriendRequest(sender, Instant.now());
    }
}

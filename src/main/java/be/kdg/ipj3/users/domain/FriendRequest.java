package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.ValueObject;

import java.time.Instant;

@ValueObject
public record FriendRequest(UserId sender, Instant createdAt) {
    public FriendRequest(UserId sender) {
        this(sender, Instant.now());
    }
}

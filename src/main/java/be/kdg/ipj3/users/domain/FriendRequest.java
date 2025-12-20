package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.users.shared.UserId;
import org.jmolecules.ddd.annotation.ValueObject;

import java.time.Instant;

@ValueObject
public record FriendRequest(UserId otherUser, Instant createdAt) {
    public FriendRequest(UserId otherUser) {
        this(otherUser, Instant.now());
    }
}

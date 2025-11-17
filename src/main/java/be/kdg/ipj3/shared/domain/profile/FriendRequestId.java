package be.kdg.ipj3.shared.domain.profile;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record FriendRequestId(UUID friendRequestId) {
    public FriendRequestId{
        Objects.requireNonNull(friendRequestId, "friendRequestId cannot be null");
    }
}

package be.kdg.ipj3.shared.domain.profile;

import be.kdg.ipj3.shared.domain.user.UserId;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.UUID;

@Entity
public record FriendRequest(@Identity FriendRequestId friendRequestId, Instant createdAt, UserId playerFrom,
                            UserId playerTo) {
    public FriendRequest(UserId playerFrom, UserId playerTo) {
        this(new FriendRequestId(UUID.randomUUID()), Instant.now(), playerFrom, playerTo);
    }

    @Override
    public FriendRequestId friendRequestId() {
        return friendRequestId;
    }
}

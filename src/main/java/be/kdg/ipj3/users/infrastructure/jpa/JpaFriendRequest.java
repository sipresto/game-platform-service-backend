package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.users.domain.FriendRequest;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;
import java.util.UUID;

@Embeddable
public class JpaFriendRequest {
    @Column(nullable = false)
    UUID senderId;

    @Column
    Instant createdAt;

    protected JpaFriendRequest() {
    } // for JPA

    public JpaFriendRequest(UUID senderId, Instant createdAt) {
        this.senderId = senderId;
        this.createdAt = createdAt;
    }

    public static JpaFriendRequest fromDomain(FriendRequest friendRequest) {
        return new JpaFriendRequest(friendRequest.otherUser().id(), friendRequest.createdAt());
    }

    public FriendRequest toDomain() {
        return new FriendRequest(new UserId(senderId), createdAt);
    }
}


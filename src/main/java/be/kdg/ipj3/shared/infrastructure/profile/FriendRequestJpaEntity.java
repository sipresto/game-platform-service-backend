package be.kdg.ipj3.shared.infrastructure.profile;

import be.kdg.ipj3.shared.domain.profile.FriendRequest;
import be.kdg.ipj3.shared.domain.profile.FriendRequestId;
import be.kdg.ipj3.shared.domain.user.UserId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "friend_requests")
public class FriendRequestJpaEntity {
    @Id
    private UUID friendRequestId;

    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_from_id", referencedColumnName = "userId")
    private ProfileJpaEntity playerFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_to_id", referencedColumnName = "userId")
    private ProfileJpaEntity playerTo;

    public FriendRequestJpaEntity(UUID friendRequestId, Instant createdAt, ProfileJpaEntity playerFrom, ProfileJpaEntity playerTo) {
        this.friendRequestId = friendRequestId;
        this.createdAt = createdAt;
        this.playerFrom = playerFrom;
        this.playerTo = playerTo;
    }

    protected FriendRequestJpaEntity() {
    }

    public static FriendRequestJpaEntity fromDomain(FriendRequest friendRequest, ProfileJpaEntity fromProfile, ProfileJpaEntity toProfile) {
        return new FriendRequestJpaEntity(
                friendRequest.friendRequestId().friendRequestId(),
                friendRequest.createdAt(),
                fromProfile,
                toProfile
        );
    }

    public FriendRequest toDomain() {
        return new FriendRequest(
                new FriendRequestId(friendRequestId),
                createdAt,
                new UserId(this.playerFrom.getUserId()),
                new UserId(this.playerTo.getUserId())
        );
    }

    public UUID getFriendRequestId() {
        return friendRequestId;
    }

    public void setFriendRequestId(UUID friendRequestId) {
        this.friendRequestId = friendRequestId;
    }
}
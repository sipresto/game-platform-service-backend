package be.kdg.ipj3.users.api.dto;

import be.kdg.ipj3.users.domain.FriendRequest;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.shared.UserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserDto(
        @NotNull UUID id,
        @NotBlank String keycloakId,
        @NotBlank String username,
        @Valid @NotNull ProfileDto profile,
        @NotNull Visibility visibility,
        @NotNull int points,
        List<IncomingFriendRequestDto> incomingFriendRequests,
        List<UUID> friends,
        List<OutgoingFriendRequestDto> outgoingFriendRequests
) {
    public static UserDto fromDomain(User user) {
        return new UserDto(
                user.getId().id(),
                user.getKeycloakId(),
                user.getUsername(),
                ProfileDto.fromDomain(user.getProfile()),
                user.getVisibility(),
                user.getPoints(),
                user.getIncomingFriendRequests().stream().map(IncomingFriendRequestDto::fromDomain).toList(),
                user.getFriends().stream().map(UserId::id).toList(),
                user.getOutgoingFriendRequests().stream().map(OutgoingFriendRequestDto::fromDomain).toList()
        );
    }

    public record ProfileDto(
            @NotBlank String displayName,
            String bio
    ) {
        public static ProfileDto fromDomain(Profile profile) {
            return new ProfileDto(
                    profile.displayName(),
                    profile.bio()
            );
        }
    }

    public record IncomingFriendRequestDto(
            @NotNull UUID senderId,
            @NotNull Instant createdAt
    ) {
        public static IncomingFriendRequestDto fromDomain(FriendRequest friendRequest) {
            return new IncomingFriendRequestDto(
                    friendRequest.otherUser().id(),
                    friendRequest.createdAt()
            );
        }
    }

    public record OutgoingFriendRequestDto(
            @NotNull UUID receiverId,
            @NotNull Instant createdAt
    ) {
        public static OutgoingFriendRequestDto fromDomain(FriendRequest friendRequest) {
            return new OutgoingFriendRequestDto(
                    friendRequest.otherUser().id(),
                    friendRequest.createdAt()
            );
        }
    }
}

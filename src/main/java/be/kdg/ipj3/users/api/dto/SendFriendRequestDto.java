package be.kdg.ipj3.users.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendFriendRequestDto(@NotNull UUID receiverId, @NotNull UUID senderId) {
}

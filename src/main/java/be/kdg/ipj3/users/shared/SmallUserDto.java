package be.kdg.ipj3.users.shared;

import be.kdg.ipj3.users.domain.User;

import java.util.UUID;

public record SmallUserDto(
        UUID id,
        String keycloakId,
        String username,
        String displayName
) {
    public static SmallUserDto fromDomain(User user) {
        return new SmallUserDto(user.getId().id(), user.getKeycloakId(), user.getUsername(), user.getProfile().displayName());
    }
}

package be.kdg.ipj3.users.shared;

import be.kdg.ipj3.users.domain.User;

public record SmallUserDto(
        String keycloakId,
        String username,
        String displayName
) {
    public static SmallUserDto fromDomain(User user) {
        return new SmallUserDto(user.getKeycloakId(), user.getUsername(), user.getProfile().displayName());
    }
}

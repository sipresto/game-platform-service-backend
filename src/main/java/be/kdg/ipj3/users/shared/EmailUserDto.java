package be.kdg.ipj3.users.shared;

import be.kdg.ipj3.users.domain.User;

public record EmailUserDto(
        String keycloakId,
        String email,
        String username
) {

    public static EmailUserDto fromDomain(User user) {
        return new EmailUserDto(user.getKeycloakId(), user.getEmail(), user.getUsername());
    }
}

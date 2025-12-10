package be.kdg.ipj3.users.shared;

import be.kdg.ipj3.users.domain.User;

public record SmallUserDto(
        UserId id,
        String name
) {
    public static SmallUserDto fromDomain(User user) {
        return new SmallUserDto(user.getId(), user.getUsername());
    }
}

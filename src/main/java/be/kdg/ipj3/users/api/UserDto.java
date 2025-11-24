package be.kdg.ipj3.users.api;

import be.kdg.ipj3.catalog.api.GameDto;
import be.kdg.ipj3.shared.api.SmallGameDto;
import be.kdg.ipj3.users.domain.User;

import java.util.List;
import java.util.UUID;

public record UserDto(UUID userId, String username, List<SmallGameDto> gameDto) {
    public static UserDto from(User user, List<SmallGameDto> games) {
        return new UserDto(
                user.getId().id(),
                user.getUsername(),
                games
        );
    }
}

package be.kdg.ipj3.shared.api;

import be.kdg.ipj3.catalog.domain.Game;

import java.util.UUID;

public record SmallGameDto(UUID gameId, String name) {
    public static SmallGameDto fromDomain(Game game) {
        return new SmallGameDto(
                game.getId().id(),
                game.getName()
        );
    }
}

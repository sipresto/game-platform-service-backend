package be.kdg.ipj3.catalog.api.dto;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;

import java.util.List;
import java.util.UUID;

public record LibraryGameDto(
        UUID gameId,
        String name,
        String description,
        List<Category> categories,
        boolean isFavorited
) {
    public static LibraryGameDto fromDomain(Game game) {
        return new LibraryGameDto(
                game.getId().id(),
                game.getName(),
                game.getDescription(),
                game.getCategories(),
                true
        );
    }
}

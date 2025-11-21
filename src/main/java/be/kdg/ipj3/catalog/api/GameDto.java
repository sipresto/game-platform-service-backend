package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record GameDto(UUID gameId, String name, String description, BigDecimal price, List<Category> categories) {
    public static GameDto from(Game game) {
        return new GameDto(
                game.getId().id(),
                game.getName(),
                game.getDescription(),
                game.getPrice().amount(),
                game.getCategories());
    }
}

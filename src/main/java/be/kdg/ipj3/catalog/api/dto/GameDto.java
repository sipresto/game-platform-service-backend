package be.kdg.ipj3.catalog.api.dto;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record GameDto(UUID gameId, String name, String description, BigDecimal price, String rules, List<Category> categories) {
    public static GameDto from(Game game) {
        return new GameDto(
                game.getId().id(),
                game.getName(),
                game.getDescription(),
                game.getPrice().amount(),
                game.getRules(),
                game.getCategories()
        );
    }
}

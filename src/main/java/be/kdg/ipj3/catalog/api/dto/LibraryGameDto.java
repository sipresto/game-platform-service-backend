package be.kdg.ipj3.catalog.api.dto;

import be.kdg.ipj3.catalog.domain.Category;

import java.util.List;
import java.util.UUID;

public record LibraryGameDto(
        UUID gameId,
        String name,
        String description,
        List<Category> categories,
        boolean isFavorited
) {
}

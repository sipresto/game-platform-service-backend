package be.kdg.ipj3.catalog.api.dto;

import be.kdg.ipj3.catalog.domain.Category;

import java.math.BigDecimal;
import java.util.List;

public record GameFilterRequestDto(
        BigDecimal minPrice,
        BigDecimal maxPrice,
        List<Category> categories
) {
}

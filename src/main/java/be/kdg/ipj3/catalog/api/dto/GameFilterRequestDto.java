package be.kdg.ipj3.catalog.api.dto;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.shared.domain.Money;

import java.util.List;

public record GameFilterRequestDto(Money minPrice, Money maxPrice, List<Category> categories) {
}

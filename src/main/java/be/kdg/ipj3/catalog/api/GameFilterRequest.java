package be.kdg.ipj3.catalog.api;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.shared.domain.Money;

import java.util.List;

public record GameFilterRequest(Money minPrice, Money maxPrice, List<Category> categories) {
}

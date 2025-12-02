package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTests {

    @Test
    void constructor_shouldCreateGameWithGeneratedId() {
        // Arrange
        String name = "Test Game";
        String description = "Test Description";
        Money price = Money.of(BigDecimal.valueOf(49.99));
        String serviceUrl = "http://test.com/service";
        List<Category> categories = List.of(Category.CARDGAME);

        // Act
        Game game = new Game(name, description, price, serviceUrl, categories);

        // Assert
        assertNotNull(game.getId());
        assertEquals(name, game.getName());
        assertEquals(description, game.getDescription());
        assertEquals(price, game.getPrice());
        assertEquals(serviceUrl, game.getServiceUrl());
        assertEquals(categories, game.getCategories());
        assertTrue(game.getAchievements().isEmpty());
    }
}

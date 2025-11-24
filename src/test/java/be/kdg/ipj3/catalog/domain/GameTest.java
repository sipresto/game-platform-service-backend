package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game("Game1", "Action Game", Money.of(BigDecimal.valueOf(4.99)), List.of(Category.BOARDGAME), "http://localhost/api/game1");
    }

    @Test
    void testGameConstructor_WithAllFields() {
        // Arrange
        List<Category> categories = List.of(Category.BOARDGAME, Category.EXTERNAL);
        Money price = Money.of(BigDecimal.valueOf(5.99));

        // Act
        Game game = new Game("Game2", "Adventure Game", price, categories, "http://localhost/api/game2");

        // Assert
        assertNotNull(game.getId());
        assertEquals("Game2", game.getName());
        assertEquals("Adventure Game", game.getDescription());
        assertEquals(price, game.getPrice());
        assertEquals(categories, game.getCategories());
        assertTrue(game.getAchievements().isEmpty());
        assertEquals("http://localhost/api/game2", game.getServiceUrl());
    }

    @Test
    void testGameConstructor_MinimalFields() {
        // Act
        Game game = new Game("Game3", "Strategy Game", Money.of(BigDecimal.valueOf(1.99)), List.of(Category.STRATEGY), "http://localhost/api/game3");

        // Assert
        assertEquals("Game3", game.getName());
        assertEquals("Strategy Game", game.getDescription());
        assertEquals(Money.of(BigDecimal.valueOf(1.99)), game.getPrice());
    }

    @Test
    void testGameHasValidIdOnCreation() {
        // Act
        Game game = new Game("Game4", "Puzzle Game", Money.of(BigDecimal.valueOf(1.99)), List.of(Category.BOARDGAME), "http://localhost/api/game4");

        // Assert
        assertNotNull(game.getId());
    }
}

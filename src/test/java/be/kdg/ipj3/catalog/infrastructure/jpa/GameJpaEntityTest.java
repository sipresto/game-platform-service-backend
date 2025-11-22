package be.kdg.ipj3.catalog.infrastructure.jpa;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class GameJpaEntityTest {

    @Test
    void testFromDomain() {
        // Arrange
        GameId gameId = new GameId(UUID.randomUUID());
        Money price = Money.of(new BigDecimal("19.99"));
        Category category1 = Category.BOARDGAME;
        Category category2 = Category.NATIVE;

        List<Achievement> achievements = List.of();
        List<Category> categories = List.of(category1, category2);

        Game game = new Game(gameId, "Game Name", "Description", price, achievements, categories);

        // Act
        GameJpaEntity gameJpaEntity = GameJpaEntity.fromDomain(game);

        // Assert
        assertEquals(game.getId().id(), gameJpaEntity.getGameId());
        assertEquals(game.getName(), gameJpaEntity.getName());
        assertEquals(game.getDescription(), gameJpaEntity.getDescription());
        assertEquals(game.getPrice().amount(), gameJpaEntity.getPrice());
        assertTrue(gameJpaEntity.getCategories().contains("boardgame"));
        assertTrue(gameJpaEntity.getCategories().contains("native"));
    }

    @Test
    void testToDomain() {
        //Arrange
        GameJpaEntity gameJpaEntity = new GameJpaEntity(
                new GameId(UUID.randomUUID()),
                "Game Name",
                "Description",
                Money.of(new BigDecimal("19.99")),
                List.of(),
                List.of(Category.BOARDGAME, Category.NATIVE)
        );

        //Act
        Game game = gameJpaEntity.toDomain();

        //Assert
        assertNotNull(game);
        assertEquals(gameJpaEntity.getGameId(), game.getId().id());
        assertEquals(gameJpaEntity.getName(), game.getName());
        assertEquals(gameJpaEntity.getDescription(), game.getDescription());
        assertEquals(gameJpaEntity.getPrice(), game.getPrice().amount());
        assertEquals(2, game.getCategories().size());
        assertTrue(game.getCategories().contains(Category.find("boardgame")));
        assertTrue(game.getCategories().contains(Category.find("native")));
    }

    @Test
    void testEmptyCategories() {
        //Arrange
        GameJpaEntity gameJpaEntity = new GameJpaEntity(
                new GameId(UUID.randomUUID()),
                "Game Name",
                "Description",
                Money.of(new BigDecimal("19.99")),
                List.of(),
                List.of()
        );

        //Act & Assert
        assertTrue(gameJpaEntity.toDomain().getCategories().isEmpty());
    }
}
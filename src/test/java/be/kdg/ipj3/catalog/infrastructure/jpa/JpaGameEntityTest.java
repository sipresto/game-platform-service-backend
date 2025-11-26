/*
package be.kdg.ipj3.catalog.infrastructure.jpa;

import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class JpaGameEntityTest {

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
        JpaGameEntity jpaGameEntity = JpaGameEntity.fromDomain(game);

        // Assert
        assertEquals(game.getId().id(), jpaGameEntity.getGameId());
        assertEquals(game.getName(), jpaGameEntity.getName());
        assertEquals(game.getDescription(), jpaGameEntity.getDescription());
        assertEquals(game.getPrice().amount(), jpaGameEntity.getPrice());
        assertTrue(jpaGameEntity.getCategories().contains("boardgame"));
        assertTrue(jpaGameEntity.getCategories().contains("native"));
    }

    @Test
    void testToDomain() {
        //Arrange
        JpaGameEntity jpaGameEntity = new JpaGameEntity(
                new GameId(UUID.randomUUID()),
                "Game Name",
                "Description",
                Money.of(new BigDecimal("19.99")),
                List.of(),
                List.of(Category.BOARDGAME, Category.NATIVE)
        );

        //Act
        Game game = jpaGameEntity.toDomain();

        //Assert
        assertNotNull(game);
        assertEquals(jpaGameEntity.getGameId(), game.getId().id());
        assertEquals(jpaGameEntity.getName(), game.getName());
        assertEquals(jpaGameEntity.getDescription(), game.getDescription());
        assertEquals(jpaGameEntity.getPrice(), game.getPrice().amount());
        assertEquals(2, game.getCategories().size());
        assertTrue(game.getCategories().contains(Category.find("boardgame")));
        assertTrue(game.getCategories().contains(Category.find("native")));
    }

    @Test
    void testEmptyCategories() {
        //Arrange
        JpaGameEntity jpaGameEntity = new JpaGameEntity(
                new GameId(UUID.randomUUID()),
                "Game Name",
                "Description",
                Money.of(new BigDecimal("19.99")),
                List.of(),
                List.of()
        );

        //Act & Assert
        assertTrue(jpaGameEntity.toDomain().getCategories().isEmpty());
    }
}*/

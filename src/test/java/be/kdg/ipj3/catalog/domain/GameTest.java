/*
package be.kdg.ipj3.catalog.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;
    private GameId gameId;

    @BeforeEach
    void setUp() {
        gameId = GameId.create();
        game = new Game(gameId, "Game1", "Action Game", Money.of(BigDecimal.valueOf(4.99)), List.of(), List.of(Category.BOARDGAME));
    }

    @Test
    void testGameConstructor_WithAllFields() {
        // Arrange
        List<Category> categories = List.of(Category.BOARDGAME, Category.EXTERNAL);
        Money price = Money.of(BigDecimal.valueOf(5.99));
        GameId id = GameId.create();

        // Act
        Game game = new Game(id, "Game2", "Adventure Game", price, List.of(), categories);

        // Assert
        assertEquals(id, game.getId());
        assertEquals("Game2", game.getName());
        assertEquals("Adventure Game", game.getDescription());
        assertEquals(price, game.getPrice());
        assertEquals(categories, game.getCategories());
        assertTrue(game.getAchievements().isEmpty());
    }

    @Test
    void testGameConstructor_MinimalFields() {
        // Act
        Game game = new Game("Game3", "Strategy Game", Money.of(BigDecimal.valueOf(1.99)), List.of(Category.STRATEGY));

        // Assert
        assertNotNull(game.getId());
        assertEquals("Game3", game.getName());
        assertEquals("Strategy Game", game.getDescription());
        assertEquals(Money.of(BigDecimal.valueOf(1.99)), game.getPrice());
    }

    @Test
    void testGameHasValidIdOnCreation() {
        // Act
        Game game = new Game("Game4", "Puzzle Game", Money.of(BigDecimal.valueOf(1.99)), List.of(Category.BOARDGAME));

        // Assert
        assertNotNull(game.getId());
    }

    @Test
    void testHasCategory_WhenGameHasCategory_ShouldReturnTrue() {
        // Act & Assert
        assertTrue(game.hasCategory(Category.BOARDGAME));
    }

    @Test
    void testHasCategory_WhenGameDoesNotHaveCategory_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(game.hasCategory(Category.STRATEGY));
        assertFalse(game.hasCategory(Category.EXTERNAL));
    }

    @Test
    void testHasCategory_WithMultipleCategories() {
        // Arrange
        Game multiCategoryGame = new Game("MultiGame", "Multi Category Game",
                Money.of(BigDecimal.valueOf(9.99)),
                List.of(Category.BOARDGAME, Category.STRATEGY, Category.NATIVE));

        // Act & Assert
        assertTrue(multiCategoryGame.hasCategory(Category.BOARDGAME));
        assertTrue(multiCategoryGame.hasCategory(Category.STRATEGY));
        assertTrue(multiCategoryGame.hasCategory(Category.NATIVE));
        assertFalse(multiCategoryGame.hasCategory(Category.EXTERNAL));
    }

    @Test
    void testHasAchievements_WhenNoAchievements_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(game.hasAchievements());
        assertTrue(game.getAchievements().isEmpty());
    }

    @Test
    void testHasAchievements_WhenAchievementsAdded_ShouldReturnTrue() {
        // Arrange
        Achievement achievement = new Achievement("First Win", "Win your first game", 10, gameId);

        // Act
        game.addAchievement(achievement);

        // Assert
        assertTrue(game.hasAchievements());
        assertFalse(game.getAchievements().isEmpty());
        assertEquals(1, game.getAchievements().size());
    }

    @Test
    void testHasAchievements_WithMultipleAchievements() {
        // Arrange
        Achievement achievement1 = new Achievement("First Win", "Win your first game", 10, gameId);
        Achievement achievement2 = new Achievement("Speed Runner", "Complete the game under 1 hour", 20, gameId);

        // Act
        game.addAchievement(achievement1);
        game.addAchievement(achievement2);

        // Assert
        assertTrue(game.hasAchievements());
        assertEquals(2, game.getAchievements().size());
    }

    @Test
    void testAddAchievement() {
        // Arrange
        Achievement achievement = new Achievement("Master", "Complete all levels", 50, gameId);
        int initialSize = game.getAchievements().size();

        // Act
        game.addAchievement(achievement);

        // Assert
        assertEquals(initialSize + 1, game.getAchievements().size());
        assertTrue(game.getAchievements().contains(achievement));
    }

    @Test
    void testAddMultipleAchievements() {
        // Arrange
        Achievement achievement1 = new Achievement("Achievement 1", "Description 1", 5, gameId);
        Achievement achievement2 = new Achievement("Achievement 2", "Description 2", 10, gameId);

        // Act
        game.addAchievement(achievement1);
        game.addAchievement(achievement2);

        // Assert
        assertEquals(2, game.getAchievements().size());
        assertTrue(game.getAchievements().contains(achievement1));
        assertTrue(game.getAchievements().contains(achievement2));
    }

    @Test
    void testGameWithInitialAchievements() {
        // Arrange
        GameId newGameId = GameId.create();
        List<Achievement> initialAchievements = List.of(
                new Achievement("Starter", "Start the game", 5, newGameId),
                new Achievement("Explorer", "Visit all locations", 15, newGameId)
        );
        Game gameWithAchievements = new Game(
                newGameId,
                "GameWithAchievements",
                "Game with initial achievements",
                Money.of(BigDecimal.valueOf(14.99)),
                initialAchievements,
                List.of(Category.STRATEGY)
        );

        // Act & Assert
        assertTrue(gameWithAchievements.hasAchievements());
        assertEquals(2, gameWithAchievements.getAchievements().size());
        assertTrue(gameWithAchievements.getAchievements().containsAll(initialAchievements));
    }

    @Test
    void testAchievementProperties() {
        // Arrange
        Achievement achievement = new Achievement("Test Achievement", "Test Description", 25, gameId);

        // Act & Assert
        assertNotNull(achievement.getId());
        assertEquals("Test Achievement", achievement.getName());
        assertEquals("Test Description", achievement.getDescription());
        assertEquals(25, achievement.getPoints());
        assertEquals(gameId, achievement.getGameId());
    }

    @Test
    void testAchievementPointsModification() {
        // Arrange
        Achievement achievement = new Achievement("Test Achievement", "Test Description", 25, gameId);

        // Act
        achievement.addPoints(10);

        // Assert
        assertEquals(35, achievement.getPoints());

        // Act
        achievement.removePoints(5);

        // Assert
        assertEquals(30, achievement.getPoints());
    }
}*/

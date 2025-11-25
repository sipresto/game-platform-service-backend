package be.kdg.ipj3.catalog.infrastructure.jpa;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(GameRepository.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class GameRepositoryTests {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void save_shouldPersistGame_whenGameIsValid() {
        // Arrange
        GameId gameId = GameId.create();
        Game newGame = new Game(
                gameId,
                "Test Game",
                "Test Description",
                Money.of(BigDecimal.valueOf(4.99)),
                "http://test.com/service",
                List.of(),
                List.of(Category.BOARDGAME)
        );

        // Act
        gameRepository.save(newGame);

        // Assert
        JpaGameEntity foundGame = entityManager.find(JpaGameEntity.class, gameId.id());
        assertThat(foundGame).isNotNull();
        assertThat(foundGame.getName()).isEqualTo("Test Game");
        assertThat(foundGame.getDescription()).isEqualTo("Test Description");
        assertThat(foundGame.getPrice()).isEqualTo(BigDecimal.valueOf(4.99));
        assertThat(foundGame.getServiceUrl()).isEqualTo("http://test.com/service");
        assertThat(foundGame.getCategories()).isEqualTo("boardgame, ");
    }

    @Test
    void save_shouldUpdateGame_whenGameAlreadyExists() {
        // Arrange
        GameId gameId = GameId.create();
        Game originalGame = new Game(
                gameId,
                "Original Game",
                "Original Description",
                Money.of(BigDecimal.valueOf(9.99)),
                "http://test.com/service",
                List.of(),
                List.of(Category.BOARDGAME)
        );
        gameRepository.save(originalGame);

        Game updatedGame = new Game(
                gameId,
                "Updated Game",
                "Updated Description",
                Money.of(BigDecimal.valueOf(14.99)),
                "http://test.com/service2",
                List.of(),
                List.of(Category.CARDGAME)
        );

        // Act
        gameRepository.save(updatedGame);

        // Assert
        JpaGameEntity foundGame = entityManager.find(JpaGameEntity.class, gameId.id());
        assertThat(foundGame).isNotNull();
        assertThat(foundGame.getName()).isEqualTo("Updated Game");
        assertThat(foundGame.getDescription()).isEqualTo("Updated Description");
        assertThat(foundGame.getPrice()).isEqualTo(BigDecimal.valueOf(14.99));
        assertThat(foundGame.getServiceUrl()).isEqualTo("http://test.com/service2");
        assertThat(foundGame.getCategories()).isEqualTo("cardgame, ");
    }

    @Test
    void findAllGames_shouldReturnEmptyList_whenNoGamesExist() {
        // Act
        List<Game> games = gameRepository.findAllGames();

        // Assert
        assertThat(games).isNotNull();
        assertThat(games.size()).isEqualTo(0);
    }

    @Test
    void findAllGames_shouldReturnAllGames_whenGamesExist() {
        // Arrange
        GameId gameId1 = GameId.create();
        Game game1 = new Game(
                gameId1,
                "Game One",
                "First Game",
                Money.of(BigDecimal.valueOf(5.99)),
                "http://test1.com/service",
                List.of(),
                List.of(Category.BOARDGAME)
        );
        GameId gameId2 = GameId.create();
        Game game2 = new Game(
                gameId2,
                "Game Two",
                "Second Game",
                Money.of(BigDecimal.valueOf(7.99)),
                "http://test2.com/service",
                List.of(),
                List.of(Category.CARDGAME)
        );
        gameRepository.save(game1);
        gameRepository.save(game2);

        // Act
        List<Game> games = gameRepository.findAllGames();

        // Assert
        assertThat(games).isNotNull();
        assertThat(games.size()).isEqualTo(2);
        assertThat(games.stream().anyMatch(g -> g.getName().equals("Game One"))).isTrue();
        assertThat(games.stream().anyMatch(g -> g.getName().equals("Game Two"))).isTrue();
    }

    @Test
    void findAllGames_shouldReturnGamesWithCorrectDomainMapping() {
        // Arrange
        GameId gameId = GameId.create();
        Game game = new Game(
                gameId,
                "Test Game",
                "Test Description",
                Money.of(BigDecimal.valueOf(9.99)),
                "http://test.com/service",
                List.of(),
                List.of(Category.BOARDGAME, Category.CARDGAME)
        );
        gameRepository.save(game);

        // Act
        List<Game> games = gameRepository.findAllGames();

        // Assert
        assertThat(games.size()).isEqualTo(1);
        Game foundGame = games.getFirst();
        assertThat(foundGame.getId().id()).isEqualTo(gameId.id());
        assertThat(foundGame.getName()).isEqualTo("Test Game");
        assertThat(foundGame.getDescription()).isEqualTo("Test Description");
        assertThat(foundGame.getPrice().amount()).isEqualTo(BigDecimal.valueOf(9.99));
        assertThat(foundGame.getServiceUrl()).isEqualTo("http://test.com/service");
        assertThat(foundGame.getCategories().size()).isEqualTo(2);
    }

    @Test
    void findServiceUrlByGameId_shouldReturnEmpty_whenGameDoesNotExist() {
        // Arrange
        GameId nonExistentId = GameId.create();

        // Act
        Optional<String> serviceUrl = gameRepository.findServiceUrlByGameId(nonExistentId);

        // Assert
        assertThat(serviceUrl).isEmpty();
    }

    @Test
    void findServiceUrlByGameId_shouldReturnServiceUrl_whenGameExists() {
        // Arrange
        GameId gameId = GameId.create();
        String expectedUrl = "http://test.com/service";
        Game game = new Game(
                gameId,
                "Test Game",
                "Test Description",
                Money.of(BigDecimal.valueOf(9.99)),
                expectedUrl,
                List.of(),
                List.of(Category.BOARDGAME)
        );
        gameRepository.save(game);

        // Act
        Optional<String> serviceUrl = gameRepository.findServiceUrlByGameId(gameId);

        // Assert
        assertThat(serviceUrl).isPresent();
        assertThat(serviceUrl.get()).isEqualTo(expectedUrl);
    }
}

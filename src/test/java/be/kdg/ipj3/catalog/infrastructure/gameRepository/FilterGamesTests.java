package be.kdg.ipj3.catalog.infrastructure.gameRepository;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.infrastructure.GameRepositoryImpl;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(GameRepositoryImpl.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class FilterGamesTests {

    @Autowired
    GameRepositoryImpl gameRepository;

    private void createTestGame(String name, BigDecimal price, List<Category> categories) {
        Game game = new Game(
                name,
                "Description",
                Money.of(price),
                "http://test.com/service",
                "A rule",
                categories
        );
        gameRepository.save(game);

    }

    @Test
    void filterGames_shouldReturnAllGames_whenNoFiltersApplied() {
        // Arrange
        createTestGame("Game 1", BigDecimal.valueOf(5.99), List.of(Category.BOARDGAME));
        createTestGame("Game 2", BigDecimal.valueOf(10.99), List.of(Category.BOARDGAME));
        createTestGame("Game 3", BigDecimal.valueOf(15.99), List.of(Category.CARDGAME));

        // Act
        List<Game> result = gameRepository.filterGames(null, null, null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    void filterGames_shouldReturnMatchingGames_whenFilteringByPriceAndCategory() {
        // Arrange
        createTestGame("Game 1", BigDecimal.valueOf(5.99), List.of(Category.BOARDGAME));
        createTestGame("Game 2", BigDecimal.valueOf(10.99), List.of(Category.BOARDGAME));
        createTestGame("Game 3", BigDecimal.valueOf(15.99), List.of(Category.CARDGAME));
        createTestGame("Game 4", BigDecimal.valueOf(27.99), List.of(Category.BOARDGAME));

        // Act
        List<Game> result = gameRepository.filterGames(
                Money.of(BigDecimal.valueOf(10.00)),
                Money.of(BigDecimal.valueOf(25.00)),
                List.of(Category.BOARDGAME)
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getName()).isEqualTo("Game 2");
    }

    @Test
    void filterGames_shouldReturnDistinctGames_whenGameHasMultipleMatchingCategories() {
        // Arrange
        createTestGame("Game 1", BigDecimal.valueOf(5.99), List.of(Category.BOARDGAME));
        createTestGame("Game 2", BigDecimal.valueOf(10.99), List.of(Category.BOARDGAME, Category.NATIVE));
        createTestGame("Game 3", BigDecimal.valueOf(15.99), List.of(Category.CARDGAME));

        // Act
        List<Game> result = gameRepository.filterGames(
                null,
                null,
                List.of(Category.BOARDGAME, Category.NATIVE)
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.stream().anyMatch(g -> g.getName().equals("Game 1"))).isTrue();
        assertThat(result.stream().anyMatch(g -> g.getName().equals("Game 2"))).isTrue();
    }
}

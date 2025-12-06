package be.kdg.ipj3.catalog.infrastructure.gameRepository;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
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
@Import(GameRepository.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class FindAllGamesTests {

    @Autowired
    GameRepository gameRepository;

    private void createTestGame(String name) {
        Game game = new Game(
                name,
                "Description",
                Money.of(BigDecimal.valueOf(10.99)),
                "http://test.com/service",
                "A rule",
                List.of(Category.BOARDGAME)
        );
        gameRepository.save(game);

    }

    @Test
    void findAllGames_shouldReturnAllGames_whenGamesExist() {
        // Arrange
        createTestGame("Game 1");
        createTestGame("Game 2");

        // Act
        List<Game> games = gameRepository.findAllGames();

        // Assert
        assertThat(games).isNotNull();
        assertThat(games.size()).isEqualTo(2);
        assertThat(games.stream().anyMatch(g -> g.getName().equals("Game 1"))).isTrue();
        assertThat(games.stream().anyMatch(g -> g.getName().equals("Game 2"))).isTrue();
    }

    @Test
    void findAllGames_shouldReturnEmptyList_whenNoGamesExist() {
        // Act
        List<Game> games = gameRepository.findAllGames();

        // Assert
        assertThat(games).isNotNull();
        assertThat(games.size()).isEqualTo(0);
    }
}

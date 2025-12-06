package be.kdg.ipj3.catalog.infrastructure.gameRepository;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.infrastructure.GameRepository;
import be.kdg.ipj3.shared.domain.GameId;
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
public class FindGamesByIdsTests {

    @Autowired
    GameRepository gameRepository;

    private GameId createTestGame(String name) {
        Game game = new Game(
                name,
                "Description",
                Money.of(BigDecimal.valueOf(10.99)),
                "http://test.com/service",
                "A rule",
                List.of(Category.BOARDGAME)
        );
        gameRepository.save(game);
        return game.getId();
    }

    @Test
    void findGamesByIds_shouldReturnMatchingGames_whenMultipleIdsMatch() {
        // Arrange
        GameId gameId1 = createTestGame("Game 1");
        GameId gameId2 = createTestGame("Game 2");
        createTestGame("Game 3");

        // Act
        List<Game> result = gameRepository.findGamesByIds(List.of(gameId1, gameId2));

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.stream().anyMatch(g -> g.getName().equals("Game 1"))).isTrue();
        assertThat(result.stream().anyMatch(g -> g.getName().equals("Game 2"))).isTrue();
    }

    @Test
    void findGamesByIds_shouldReturnEmptyList_whenNoIdsProvided() {
        // Arrange
        createTestGame("Game 1");

        // Act
        List<Game> result = gameRepository.findGamesByIds(List.of());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
    }
}

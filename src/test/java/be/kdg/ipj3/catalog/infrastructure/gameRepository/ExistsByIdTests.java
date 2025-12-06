package be.kdg.ipj3.catalog.infrastructure.gameRepository;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.infrastructure.GameRepositoryImpl;
import be.kdg.ipj3.catalog.shared.GameId;
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
public class ExistsByIdTests {

    @Autowired
    GameRepositoryImpl gameRepository;

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
    void existsById_shouldReturnTrue_whenGameExists() {
        // Arrange
        GameId gameId = createTestGame("Game1");

        // Act
        boolean exists = gameRepository.existsById(gameId);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalse_whenGameDoesNotExist() {
        // Arrange
        GameId nonExistentId = GameId.create();
        createTestGame("Game1");

        // Act
        boolean exists = gameRepository.existsById(nonExistentId);

        // Assert
        assertThat(exists).isFalse();
    }
}

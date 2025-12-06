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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(GameRepositoryImpl.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class FindServiceUrlByGameIdTests {

    @Autowired
    GameRepositoryImpl gameRepository;

    private GameId createTestGame(String name, String serviceUrl) {
        Game game = new Game(
                name,
                "Description",
                Money.of(BigDecimal.valueOf(10.99)),
                serviceUrl,
                "A rule",
                List.of(Category.BOARDGAME)
        );
        gameRepository.save(game);

        return game.getId();
    }

    @Test
    void findServiceUrlByGameId_shouldReturnServiceUrl_whenGameExists() {
        // Arrange
        GameId gameId = createTestGame("Game 1", "http://test.som/service");

        // Act
        Optional<String> serviceUrl = gameRepository.findServiceUrlByGameId(gameId);

        // Assert
        assertThat(serviceUrl).contains("http://test.som/service");
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
}

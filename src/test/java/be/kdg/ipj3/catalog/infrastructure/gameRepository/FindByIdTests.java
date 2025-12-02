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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(GameRepository.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class FindByIdTests {

    @Autowired
    GameRepository gameRepository;

    @Test
    void findById_shouldReturnGame_whenGameExists() {
        // Arrange
        GameId gameId = GameId.create();
        Game game = new Game(
                gameId,
                "Test Game",
                "Test Description",
                Money.of(BigDecimal.valueOf(9.99)),
                "http://test.com/service",
                List.of(),
                List.of(Category.BOARDGAME)
        );
        gameRepository.save(game);

        // Act
        Optional<Game> result = gameRepository.findById(gameId);

        // Assert
        assertThat(result).isPresent();
        Game foundGame = result.orElseThrow();

        assertThat(foundGame.getId().id()).isEqualTo(gameId.id());
        assertThat(foundGame.getName()).isEqualTo("Test Game");
        assertThat(foundGame.getDescription()).isEqualTo("Test Description");
        assertThat(foundGame.getPrice().amount()).isEqualTo(BigDecimal.valueOf(9.99));
        assertThat(foundGame.getServiceUrl()).isEqualTo("http://test.com/service");
        assertThat(foundGame.getCategories());
    }

    @Test
    void findById_shouldReturnEmpty_whenGameDoesNotExist() {
        // Arrange
        GameId nonExistentId = GameId.create();

        // Act
        Optional<Game> result = gameRepository.findById(nonExistentId);

        // Assert
        assertThat(result).isEmpty();
    }
}

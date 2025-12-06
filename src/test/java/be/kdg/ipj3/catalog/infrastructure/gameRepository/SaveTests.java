package be.kdg.ipj3.catalog.infrastructure.gameRepository;

import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.infrastructure.GameRepositoryImpl;
import be.kdg.ipj3.catalog.infrastructure.jpa.JpaGameEntity;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
public class SaveTests {

    @Autowired
    GameRepositoryImpl gameRepository;

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
                "A rule",
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
        assertThat(foundGame.getCategories().contains(Category.BOARDGAME));
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
                "A rule",
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
                "A rule",
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
        assertThat(foundGame.getCategories().contains(Category.CARDGAME));
    }
}

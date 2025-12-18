package be.kdg.ipj3.catalog.application.gameService;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.infrastructure.rabbitmq.dto.RegisterAchievementDto;
import be.kdg.ipj3.catalog.infrastructure.rabbitmq.dto.RegisterGameDto;
import be.kdg.ipj3.catalog.shared.CountTrigger;
import be.kdg.ipj3.catalog.shared.CountingStrategy;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
class RegisterGameTests {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
    }

    private RegisterGameDto createRegisterGameDto(UUID gameId, String name) {
        return new RegisterGameDto(
                gameId,
                name,
                "Test Description",
                BigDecimal.valueOf(4.99),
                "/api/game",
                "/game",
                "Rules",
                List.of(
                        new RegisterAchievementDto(
                                "Test Achievement",
                                "TEST",
                                "Achievement Description",
                                1,
                                10,
                                "INCREMENT",
                                "PLAY_ANY"
                        )),
                List.of("NATIVE"),
                "",
                Instant.now()
        );
    }

    private Game createGame(UUID gameId) {
        Game game = new Game(
                GameId.create(),
                "Test Game",
                "Test Description",
                Money.of(BigDecimal.valueOf(4.99)),
                "/api/game",
                "/game",
                "Rules",
                List.of(
                        new Achievement(
                                "Test Achievement",
                                "TEST",
                                "Achievement Description",
                                1,
                                10,
                                CountingStrategy.INCREMENT,
                                CountTrigger.PLAY_ANY
                        )),
                List.of(Category.NATIVE),
                "",
                Instant.now(),
                false
        );

        gameRepository.save(game);
        return game;
    }

    @Test
    void registerGame_shouldAddGameToDatabase_whenRequestIsValid() {
        // Arrange
        GameId gameId = GameId.create();
        RegisterGameDto dto = createRegisterGameDto(gameId.id(), "Test Game");

        // Act
        gameService.registerGame(dto);

        // Assert
        Optional<Game> game = gameRepository.findByIdWithAchievements(gameId);

        assertThat(game).isPresent();

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Game actualGame = game.get();
        assertThat(actualGame.getName()).isEqualTo(dto.name());
        assertThat(actualGame.getAchievements().getFirst().getName()).isEqualTo(dto.achievements().getFirst().name());
    }

    @Test
    void registerGame_shouldActivateGame_whenGameAlreadyExists() {
        // Arrange
        Game existingGame = createGame(UUID.randomUUID());
        RegisterGameDto dto = createRegisterGameDto(existingGame.getId().id(), existingGame.getName());

        // Act
        gameService.registerGame(dto);

        // Assert
        Optional<Game> game = gameRepository.findByIdWithAchievements(existingGame.getId());

        assertThat(game).isPresent();

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Game actualGame = game.get();
        assertThat(actualGame.isActive()).isEqualTo(true);
    }
}

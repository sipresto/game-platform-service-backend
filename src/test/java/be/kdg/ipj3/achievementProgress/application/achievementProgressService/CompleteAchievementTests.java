package be.kdg.ipj3.achievementProgress.application.achievementProgressService;

import be.kdg.ipj3.achievementProgress.application.AchievementProgressService;
import be.kdg.ipj3.achievementProgress.domain.AchievementProgress;
import be.kdg.ipj3.achievementProgress.domain.AchievementProgressRepository;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.shared.AchievementId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.BDDAssertions.within;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class CompleteAchievementTests {

    @Autowired
    private AchievementProgressService achievementProgressService;

    @Autowired
    private AchievementProgressRepository progressRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        progressRepository.deleteAll();
        gameRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Game createTestGame() {
        Game game = new Game(
                "Test Game",
                "Test Description",
                Money.of(BigDecimal.valueOf(49.99)),
                "http://test.com/service",
                "Rules",
                List.of(Category.NATIVE)
        );
        gameRepository.save(game);

        return game;
    }

    private void createTestAchievement(Game game) {
        game.addAchievement(
                "Test Achievement",
                "TEST",
                "Test Description",
                null,
                10,
                null,
                null
        );
        gameRepository.save(game);
    }

    private UserId createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user");
        userRepository.save(user);

        return user.getId();
    }

    @Test
    void completeAchievement_shouldMarkProgressAsCompleted_whenAchievementExists() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game);
        AchievementId achievementId = game.getAchievements().getFirst().getId();

        UserId userId = createTestUser();
        Instant timestamp = Instant.now();

        // Act
        achievementProgressService.completeAchievement(userId.id(), UUID.randomUUID(), "TEST", timestamp);

        // Assert
        Optional<AchievementProgress> progress = progressRepository
                .findByUserIdAndAchievementId(userId, achievementId);

        assertThat(progress).isPresent();

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        AchievementProgress actualProgress = progress.get();
        assertThat(actualProgress.getCompletedAt()).isCloseTo(timestamp, within(5, ChronoUnit.MILLIS));
    }

    @Test
    void completeAchievement_shouldNotUpdateProgress_whenAlreadyCompleted() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game);
        AchievementId achievementId = game.getAchievements().getFirst().getId();

        UserId userId = createTestUser();
        Instant timestamp = Instant.now();
        Instant timestamp2 = Instant.now().plusSeconds(10);

        achievementProgressService.completeAchievement(userId.id(), UUID.randomUUID(), "TEST", timestamp);

        // Act
        achievementProgressService.completeAchievement(userId.id(), UUID.randomUUID(), "TEST", timestamp2);

        // Assert
        Optional<AchievementProgress> progress = progressRepository
                .findByUserIdAndAchievementId(userId, achievementId);

        assertThat(progress).isPresent();

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        AchievementProgress actualProgress = progress.get();
        assertThat(actualProgress.isCompleted()).isTrue();
        assertThat(actualProgress.getCompletedAt()).isCloseTo(timestamp, within(5, ChronoUnit.MILLIS));
    }

    @Test
    void completeAchievement_shouldNotCrash_whenAchievementDoesNotExist() {
        // Arrange
        createTestGame();

        UserId userId = createTestUser();
        Instant timestamp = Instant.now();

        // Act & Assert
        achievementProgressService.completeAchievement(userId.id(), UUID.randomUUID(), "FAKE_TEST", timestamp);
    }

    @Test
    void completeAchievement_shouldNotCrash_whenUserDoesNotExist() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game);

        UserId userId = UserId.create();
        Instant timestamp = Instant.now();

        // Act & Assert
        achievementProgressService.completeAchievement(userId.id(), UUID.randomUUID(), "TEST", timestamp);
    }
}

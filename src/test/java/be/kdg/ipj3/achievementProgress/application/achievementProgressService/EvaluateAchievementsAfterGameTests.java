package be.kdg.ipj3.achievementProgress.application.achievementProgressService;

import be.kdg.ipj3.achievementProgress.application.AchievementProgressService;
import be.kdg.ipj3.achievementProgress.domain.AchievementProgress;
import be.kdg.ipj3.achievementProgress.domain.GameContext;
import be.kdg.ipj3.achievementProgress.domain.AchievementProgressRepository;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.shared.AchievementId;
import be.kdg.ipj3.catalog.shared.CountTrigger;
import be.kdg.ipj3.catalog.shared.CountingStrategy;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.sessions.shared.SessionResult;
import be.kdg.ipj3.shared.domain.*;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class EvaluateAchievementsAfterGameTests {

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
                "rules",
                List.of(Category.NATIVE)
        );
        gameRepository.save(game);

        return game;
    }

    private void createTestAchievement(Game game, int requiredValue, CountingStrategy countingStrategy,
                                       CountTrigger countTrigger) {
        game.addAchievement(
                "Test Achievement",
                "TEST",
                "Test Description",
                requiredValue,
                10,
                countingStrategy,
                countTrigger
        );
        gameRepository.save(game);
    }

    private GameContext createTestGameContext(UUID gameId, UUID userId, UUID winnerId,
                                              SessionResult sessionResult, Difficulty difficulty) {
        return new GameContext(
                gameId,
                userId,
                winnerId,
                sessionResult,
                difficulty
        );
    }

    private UserId createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user", "Test@gmail.com");
        userRepository.save(user);

        return user.getId();
    }

    @Test
    void evaluateAchievementsAfterGame_shouldCreateProgressAndMarkAsCompleted_whenAchievementTriggered() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 1, CountingStrategy.INCREMENT, CountTrigger.WIN_ANY);

        UserId userId = createTestUser();
        GameContext context = createTestGameContext(game.getId().id(), userId.id(), userId.id(), SessionResult.PLAYER_WIN, null);

        AchievementId achievementId = game.getAchievements().getFirst().getId();

        // Act
        achievementProgressService.evaluateAchievementsAfterGame(context);

        // Assert
        Optional<AchievementProgress> progress = progressRepository
                .findByUserIdAndAchievementId(userId, achievementId);

        assertThat(progress).isPresent();

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        AchievementProgress actualProgress = progress.get();
        assertThat(actualProgress.getCurrentProgress()).isEqualTo(1);
        assertThat(actualProgress.getCompletedAt()).isNotNull();
    }

    @Test
    void evaluateAchievementsAfterGame_shouldNotUpdateProgress_whenAlreadyCompleted() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 1, CountingStrategy.INCREMENT, CountTrigger.WIN_PLAYER);

        UserId userId = createTestUser();
        GameContext context = createTestGameContext(game.getId().id(), userId.id(), userId.id(), SessionResult.PLAYER_WIN, null);

        AchievementId achievementId = game.getAchievements().getFirst().getId();

        // Act
        achievementProgressService.evaluateAchievementsAfterGame(context);
        achievementProgressService.evaluateAchievementsAfterGame(context);

        // Assert
        AchievementProgress progress = progressRepository
                .findByUserIdAndAchievementId(userId, achievementId)
                .orElseThrow();

        assertThat(progress.getCurrentProgress()).isEqualTo(1);
        assertThat(progress.isCompleted()).isTrue();
    }

    @Test
    void evaluateAchievementsAfterGame_shouldNotTrigger_whenTriggerConditionNotMet() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 1, CountingStrategy.INCREMENT, CountTrigger.WIN_AI);

        UserId userId = createTestUser();
        GameContext context = createTestGameContext(game.getId().id(), userId.id(), userId.id(), SessionResult.PLAYER_WIN, null);

        AchievementId achievementId = game.getAchievements().getFirst().getId();

        // Act
        achievementProgressService.evaluateAchievementsAfterGame(context);

        // Assert
        Optional<AchievementProgress> progress = progressRepository
                .findByUserIdAndAchievementId(userId, achievementId);

        assertThat(progress).isEmpty();
    }

    @Test
    void evaluateAchievementsAfterGame_shouldIncrementStreak_whenStreakContinues() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 3, CountingStrategy.STREAK, CountTrigger.WIN_OR_DRAW_ANY);

        UserId userId = createTestUser();
        GameContext winContext = createTestGameContext(game.getId().id(), userId.id(), userId.id(), SessionResult.PLAYER_WIN, null);

        AchievementId achievementId = game.getAchievements().getFirst().getId();

        // Act
        achievementProgressService.evaluateAchievementsAfterGame(winContext);
        achievementProgressService.evaluateAchievementsAfterGame(winContext);
        achievementProgressService.evaluateAchievementsAfterGame(winContext);

        // Assert
        AchievementProgress progress = progressRepository
                .findByUserIdAndAchievementId(userId, achievementId)
                .orElseThrow();

        assertThat(progress.getCurrentProgress()).isEqualTo(3);
        assertThat(progress.isCompleted()).isTrue();
    }

    @Test
    void evaluateAchievementsAfterGame_shouldResetStreak_whenStreakBroken() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 3, CountingStrategy.STREAK, CountTrigger.WIN_OR_DRAW_ANY);

        UserId userId = createTestUser();
        GameContext winContext = createTestGameContext(game.getId().id(), userId.id(), userId.id(), SessionResult.PLAYER_WIN, null);
        GameContext loseContext = createTestGameContext(game.getId().id(), userId.id(), UUID.randomUUID(), SessionResult.PLAYER_WIN, null);

        AchievementId achievementId = game.getAchievements().getFirst().getId();

        // Act
        achievementProgressService.evaluateAchievementsAfterGame(winContext);
        achievementProgressService.evaluateAchievementsAfterGame(winContext);
        achievementProgressService.evaluateAchievementsAfterGame(loseContext);
        achievementProgressService.evaluateAchievementsAfterGame(winContext);

        // Assert
        AchievementProgress progress = progressRepository
                .findByUserIdAndAchievementId(userId, achievementId)
                .orElseThrow();

        assertThat(progress.getCurrentProgress()).isEqualTo(1);
        assertThat(progress.isCompleted()).isFalse();
    }

    // TODO: add test for non existing user
}

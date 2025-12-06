package be.kdg.ipj3.achievements.application.achievementProgressService;

import be.kdg.ipj3.achievements.application.AchievementProgressService;
import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.achievements.domain.GameContext;
import be.kdg.ipj3.achievements.domain.IAchievementProgressRepository;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.IGameRepository;
import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.sessions.domain.session.SessionResult;
import be.kdg.ipj3.shared.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class EvaluateAchievementsAfterGameTests {

    @Autowired
    private AchievementProgressService achievementProgressService;

    @Autowired
    private IAchievementProgressRepository progressRepository;

    @Autowired
    private IGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        progressRepository.deleteAll();
        gameRepository.deleteAll();
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

    private GameContext createTestGameContext(GameId gameId, UserId userId, UserId winnerId,
                                              SessionResult sessionResult, Difficulty difficulty) {
        return new GameContext(
                gameId,
                userId,
                winnerId,
                sessionResult,
                difficulty
        );
    }

    @Test
    void evaluateAchievementsAfterGame_shouldCreateProgressAndMarkAsCompleted_whenAchievementTriggered() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 1, CountingStrategy.INCREMENT, CountTrigger.WIN_ANY);

        UserId userId = UserId.create();
        GameContext context = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN, null);

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

        UserId userId = UserId.create();
        GameContext context = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN, null);

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

        UserId userId = UserId.create();
        GameContext context = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN, null);

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

        UserId userId = UserId.create();
        GameContext winContext = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN, null);

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

        UserId userId = UserId.create();
        GameContext winContext = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN, null);
        GameContext loseContext = createTestGameContext(game.getId(), userId, UserId.create(), SessionResult.PLAYER_WIN, null);

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
}

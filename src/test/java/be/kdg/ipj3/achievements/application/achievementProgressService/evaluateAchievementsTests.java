/*
package be.kdg.ipj3.achievements.application.achievementProgressService;

import be.kdg.ipj3.achievements.application.AchievementProgressService;
import be.kdg.ipj3.achievements.domain.AchievementProgress;
import be.kdg.ipj3.achievements.domain.GameContext;
import be.kdg.ipj3.achievements.domain.IAchievementProgressRepository;
import be.kdg.ipj3.shared.domain.AchievementId;
import be.kdg.ipj3.shared.domain.CountTrigger;
import be.kdg.ipj3.shared.domain.CountingStrategy;
import be.kdg.ipj3.shared.domain.GameId;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class evaluateAchievementsTests {

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
                List.of(Category.NATIVE)
        );
        gameRepository.save(game);

        return game;
    }

    private void createTestAchievement(Game game, int requiredValue, int points, CountingStrategy countingStrategy,
                                       CountTrigger countTrigger, String ruleKey) {
        game.addAchievement(
                "Test Achievement",
                "Test Description",
                requiredValue,
                points,
                countingStrategy,
                countTrigger,
                ruleKey
        );
        gameRepository.save(game);
    }

    private GameContext createTestGameContext(GameId gameId, UserId userId, UserId winnerId, SessionResult sessionResult,
                                              Difficulty difficulty, Map<String, Object> metadata) {
        return new GameContext(
                gameId,
                userId,
                winnerId,
                sessionResult,
                difficulty,
                metadata
        );
    }

    @Test
    void evaluateAchievements_shouldCreateProgressAndMarkAsCompleted_whenAchievementTriggered() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 1, 10, CountingStrategy.INCREMENT,
                CountTrigger.WIN_ANY, null);

        UserId userId = UserId.create();
        GameContext context = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN,
                null, Map.of("playerSide", "X"));

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
    void evaluateAchievements_shouldNotUpdateProgress_whenAlreadyCompleted() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 1, 10, CountingStrategy.INCREMENT,
                CountTrigger.WIN_PLAYER, null);

        UserId userId = UserId.create();
        GameContext context = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN,
                null, Map.of("playerSide", "X"));

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
    void evaluateAchievements_shouldNotTrigger_whenTriggerConditionNotMet() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 1, 10, CountingStrategy.INCREMENT,
                CountTrigger.WIN_AI, null);

        UserId userId = UserId.create();
        GameContext context = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN,
                null, Map.of("playerSide", "X"));

        AchievementId achievementId = game.getAchievements().getFirst().getId();

        // Act
        achievementProgressService.evaluateAchievementsAfterGame(context);

        // Assert
        Optional<AchievementProgress> progress = progressRepository
                .findByUserIdAndAchievementId(userId, achievementId);

        assertThat(progress).isEmpty();
    }

    @Test
    void evaluateAchievements_shouldIncrementStreak_whenStreakContinues() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 3, 10, CountingStrategy.STREAK,
                CountTrigger.WIN_OR_DRAW_ANY, null);

        UserId userId = UserId.create();
        GameContext winContext = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN,
                null, Map.of("playerSide", "X"));

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
    void evaluateAchievements_shouldResetStreak_whenStreakBroken() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 3, 10, CountingStrategy.STREAK,
                CountTrigger.WIN_OR_DRAW_ANY, null);

        UserId userId = UserId.create();
        GameContext winContext = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN,
                null, Map.of("playerSide", "X"));
        GameContext loseContext = createTestGameContext(game.getId(), userId, UserId.create(), SessionResult.PLAYER_WIN,
                null, Map.of("playerSide", "X"));

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

    @Test
    void evaluateAchievements_shouldHandleConditionStrategy_whenCustomRuleMatches() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 2, 10, CountingStrategy.CONDITION,
                CountTrigger.WIN_ANY, "WIN_AS_BOTH_SIDES");

        UserId userId = UserId.create();
        GameContext contextX = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN,
                null, Map.of("playerSide", "X"));
        GameContext contextO = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN,
                null, Map.of("playerSide", "O"));

        AchievementId achievementId = game.getAchievements().getFirst().getId();

        // Act
        achievementProgressService.evaluateAchievementsAfterGame(contextX);
        achievementProgressService.evaluateAchievementsAfterGame(contextO);

        // Assert
        AchievementProgress progress = progressRepository
                .findByUserIdAndAchievementId(userId, achievementId)
                .orElseThrow();

        assertThat(progress.getCurrentProgress()).isEqualTo(2);
        assertThat(progress.isCompleted()).isTrue();
        assertThat(progress.getMetadata().get("sidesWon")).isNotNull();
    }

    @Test
    void evaluateAchievements_shouldNotCountTwice_whenConditionAlreadyMet() {
        // Arrange
        Game game = createTestGame();
        createTestAchievement(game, 2, 10, CountingStrategy.CONDITION,
                CountTrigger.WIN_ANY, "WIN_AS_BOTH_SIDES");

        UserId userId = UserId.create();
        GameContext contextX = createTestGameContext(game.getId(), userId, userId, SessionResult.PLAYER_WIN,
                null, Map.of("playerSide", "X"));

        AchievementId achievementId = game.getAchievements().getFirst().getId();

        // Act
        achievementProgressService.evaluateAchievementsAfterGame(contextX);
        achievementProgressService.evaluateAchievementsAfterGame(contextX);

        // Assert
        AchievementProgress progress = progressRepository
                .findByUserIdAndAchievementId(userId, achievementId)
                .orElseThrow();

        assertThat(progress.getCurrentProgress()).isEqualTo(1);
        assertThat(progress.isCompleted()).isFalse();
    }
}
*/

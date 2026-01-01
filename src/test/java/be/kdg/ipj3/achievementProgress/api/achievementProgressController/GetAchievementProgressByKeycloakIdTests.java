package be.kdg.ipj3.achievementProgress.api.achievementProgressController;

import be.kdg.ipj3.BaseIntegrationTest;
import be.kdg.ipj3.achievementProgress.domain.AchievementProgress;
import be.kdg.ipj3.achievementProgress.domain.AchievementProgressRepository;
import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.shared.AchievementId;
import be.kdg.ipj3.catalog.shared.CountTrigger;
import be.kdg.ipj3.catalog.shared.CountingStrategy;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetAchievementProgressByKeycloakIdTests extends BaseIntegrationTest {

    @Autowired
    private AchievementProgressRepository achievementProgressRepository;

    @BeforeEach
    void setUp() {
        achievementProgressRepository.deleteAll();
    }

    @Test
    void getAchievementProgressByKeycloakId_shouldReturnFilledAchievementProgressDtoList_WhenThereAreAchievements() throws Exception {
        // Arrange
        List<AchievementId> achievementIds = List.of(
                AchievementId.create(),
                AchievementId.create(),
                AchievementId.create()
        );

        GameId gameId = GameId.create();

        List<Achievement> achievements = List.of(
                new Achievement(
                        achievementIds.get(0),
                        "Achievement 1",
                        "ACHIEVEMENT_1",
                        "First achievement",
                        1,
                        10,
                        CountingStrategy.INCREMENT,
                        CountTrigger.WIN_PLAYER
                ),
                new Achievement(
                        achievementIds.get(1),
                        "Achievement 2",
                        "ACHIEVEMENT_2",
                        "Second achievement",
                        null,
                        20,
                        null,
                        null
                ),
                new Achievement(
                        achievementIds.get(2),
                        "Achievement 3",
                        "ACHIEVEMENT_3",
                        "Third achievement",
                        5,
                        30,
                        CountingStrategy.STREAK,
                        CountTrigger.WIN_PLAYER
                )
        );

        Game game = new Game(
                gameId,
                "Game 1",
                "Description",
                Money.of(BigDecimal.valueOf(20.00)),
                "/api/game",
                "/game",
                "A rule",
                achievements,
                List.of(Category.CARDGAME),
                null,
                Instant.now(),
                true
        );
        gameRepository.save(game);

        User user = createTestUser("keycloakId", "username", "Test@gmail.com");

        user.addGame(game.getId());
        userRepository.save(user);

        AchievementProgress achievementProgress1 = new AchievementProgress(
                user.getId(),
                achievementIds.get(0),
                1,
                Instant.now()
        );
        achievementProgressRepository.save(achievementProgress1);

        AchievementProgress achievementProgress2 = new AchievementProgress(
                user.getId(),
                achievementIds.get(2),
                1,
                null
        );
        achievementProgressRepository.save(achievementProgress2);

        // Act & Assert
        mockMvc.perform(get("/api/platform/achievements/user/" + user.getId().id())
                        .with(validJwt("keycloakId", "username")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].achievementId").value(achievementIds.get(0).id().toString()))
                .andExpect(jsonPath("$[0].gameId").value(gameId.id().toString()))
                .andExpect(jsonPath("$[0].gameName").value("Game 1"))
                .andExpect(jsonPath("$[0].achievementName").value("Achievement 1"))
                .andExpect(jsonPath("$[0].requiredValue").value(1))
                .andExpect(jsonPath("$[0].currentProgress").value(1))
                .andExpect(jsonPath("$[1].achievementId").value(achievementIds.get(1).id().toString()))
                .andExpect(jsonPath("$[1].gameId").value(gameId.id().toString()))
                .andExpect(jsonPath("$[1].achievementName").value("Achievement 2"))
                .andExpect(jsonPath("$[1].requiredValue").doesNotExist())
                .andExpect(jsonPath("$[1].currentProgress").doesNotExist())
                .andExpect(jsonPath("$[2].achievementId").value(achievementIds.get(2).id().toString()))
                .andExpect(jsonPath("$[2].gameId").value(gameId.id().toString()))
                .andExpect(jsonPath("$[2].achievementName").value("Achievement 3"))
                .andExpect(jsonPath("$[2].requiredValue").value(5))
                .andExpect(jsonPath("$[2].currentProgress").value(1));
    }

    @Test
    void getAchievementProgressByKeycloakId_shouldReturnEmptyAchievementProgressDtoList_WhenThereAreNoAchievements() throws Exception {
        // Arrange
        GameId gameId = GameId.create();

        Game game = new Game(
                gameId,
                "Game 1",
                "Description",
                Money.of(BigDecimal.valueOf(20.00)),
                "/api/game",
                "/game",
                "A rule",
                List.of(),
                List.of(Category.CARDGAME),
                "",
                Instant.now(),
                true
        );
        gameRepository.save(game);

        User user = createTestUser("keycloakId", "username", "Test@gmail.com");

        // Act & Assert
        mockMvc.perform(get("/api/platform/achievements/user/" + user.getId().id())
                        .with(validJwt("keycloakId", "username")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
package be.kdg.ipj3.achievementProgress.api.achievementProgressController;

import be.kdg.ipj3.achievementProgress.domain.AchievementProgress;
import be.kdg.ipj3.achievementProgress.domain.AchievementProgressRepository;
import be.kdg.ipj3.catalog.domain.Achievement;
import be.kdg.ipj3.catalog.domain.Category;
import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.catalog.domain.GameRepository;
import be.kdg.ipj3.catalog.shared.AchievementId;
import be.kdg.ipj3.catalog.shared.CountTrigger;
import be.kdg.ipj3.catalog.shared.CountingStrategy;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shared.domain.Money;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetAchievementProgressByKeycloakIdTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private AchievementProgressRepository achievementProgressRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        gameRepository.deleteAll();
        achievementProgressRepository.deleteAll();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor validJwt() {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
                        .subject("keycloakId")
                        .claim("preferred_username", "username"));
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
                "http://test.com/service",
                "A rule",
                achievements,
                List.of(Category.CARDGAME),
                null,
                Instant.now(),
                true
        );
        gameRepository.save(game);

        UserId userId = UserId.create();
        User user = new User(
                userId,
                "keycloakId",
                "username",
                List.of(gameId),
                0,
                new Profile("username", "Bio"),
                "Test@gmail.com"
        );
        userRepository.save(user);

        AchievementProgress achievementProgress = new AchievementProgress(
                userId,
                achievementIds.get(0),
                1,
                Instant.now()
        );
        achievementProgressRepository.save(achievementProgress);

        achievementProgress = new AchievementProgress(
                userId,
                achievementIds.get(2),
                1,
                null
        );
        achievementProgressRepository.save(achievementProgress);

        // Act && Assert
        mockMvc.perform(get("/api/achievements/user/" + user.getKeycloakId())
                        .with(validJwt()))
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

        List<Achievement> achievements = List.of();

        Game game = new Game(
                gameId,
                "Game 1",
                "Description",
                Money.of(BigDecimal.valueOf(20.00)),
                "http://test.com/service",
                "A rule",
                achievements,
                List.of(Category.CARDGAME),
                "",
                Instant.now(),
                true
        );
        gameRepository.save(game);

        UserId userId = UserId.create();
        User user = new User(
                userId,
                "keycloakId",
                "username",
                List.of(gameId),
                0,
                new Profile("username", "Bio"),
                "Test@gmail.com"
        );
        userRepository.save(user);


        // Act && Assert
        mockMvc.perform(get("/api/achievements/user/" + user.getKeycloakId())
                        .with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}

package be.kdg.ipj3.sessions.api.sessionController;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.domain.SessionRepository;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.sessions.shared.SessionStatus;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class GetSessionByUserIdTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    private JwtRequestPostProcessor validJwt() {
        return jwt().authorities(() -> "ROLE_PLAYER")
                .jwt(builder -> builder
                        .subject("keycloak-id")
                        .claim("preferred_username", "username"));
    }

    private UserId createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user", "Test@gmail.com");
        userRepository.save(user);

        return user.getId();
    }

    private void createTestSessions(UserId userId) {
        Session session1 = new Session(
                SessionId.create(),
                GameId.create(),
                List.of(userId),
                Difficulty.EASY,
                SessionStatus.PLAYER_WIN,
                userId,
                Instant.now()
        );

        Session session2 = new Session(
                SessionId.create(),
                GameId.create(),
                List.of(userId),
                Difficulty.HARD,
                SessionStatus.AI_WIN,
                null,
                Instant.now()
        );

        sessionRepository.save(session1);
        sessionRepository.save(session2);
    }

    @Test
    void getSessionsByUserId_shouldReturnListOfSessions_whenSessionsExist() throws Exception {
        // Arrange
        UserId userId = createTestUser();

        createTestSessions(userId);

        // Act & Assert
        mockMvc.perform(get("/api/platform/sessions/user").with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].playerIds[0]").value(userId.id().toString()))
                .andExpect(jsonPath("$[0].difficulty").value(Difficulty.EASY.name()))
                .andExpect(jsonPath("$[0].winnerId").value(userId.id().toString()))
                .andExpect(jsonPath("$[0].result").value(SessionStatus.PLAYER_WIN.name()))
                .andExpect(jsonPath("$[1].difficulty").value(Difficulty.HARD.name()));
    }

    @Test
    void getSessionsByUserId_shouldReturnEmptyList_whenNoSessionsExist() throws Exception {
        // Arrange
        UserId userId = createTestUser();

        // Act & Assert
        mockMvc.perform(get("/api/platform/sessions/user").with(validJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

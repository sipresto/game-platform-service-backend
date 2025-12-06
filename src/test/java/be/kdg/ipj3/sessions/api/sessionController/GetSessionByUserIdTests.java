package be.kdg.ipj3.sessions.api.sessionController;

import be.kdg.ipj3.lobbies.domain.Difficulty;
import be.kdg.ipj3.sessions.domain.SessionRepository;
import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.domain.SessionResult;
import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

/*
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
*/

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

    private UserId createTestUser() {
        User user = User.createFromKeycloak("keycloak-id", "test-user");
        userRepository.save(user);

        return user.getId();
    }

    private void createTestSessions(UserId userId) {
        Session session1 = new Session(
                GameId.create(),
                List.of(userId),
                Difficulty.EASY,
                userId,
                SessionResult.PLAYER_WIN
        );

        Session session2 = new Session(
                GameId.create(),
                List.of(userId),
                Difficulty.HARD,
                null,
                SessionResult.AI_WIN
        );

        sessionRepository.save(session1);
        sessionRepository.save(session2);
    }

    // TODO: first change the getSessionsByUserId method in controller, than update these tests
    /*@Test
    void getSessionsByUserId_shouldReturnListOfSessions_whenSessionsExist() throws Exception {
        // Arrange
        UserId userId = createTestUser();

        createTestSessions(userId);

        // Act & Assert
        mockMvc.perform(get("/api/session/{userId}", userId.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].playerIds[0]").value(userId.id().toString()))
                .andExpect(jsonPath("$[0].difficulty").value(Difficulty.EASY.name()))
                .andExpect(jsonPath("$[0].winnerId").value(userId.id().toString()))
                .andExpect(jsonPath("$[0].result").value(SessionResult.PLAYER_WIN.name()))
                .andExpect(jsonPath("$[1].difficulty").value(Difficulty.HARD.name()));
    }

    @Test
    void getSessionsByUserId_shouldReturnEmptyList_whenNoSessionsExist() throws Exception {
        // Arrange
        UserId userId = createTestUser();

        // Act & Assert
        mockMvc.perform(get("/api/session/{userId}", userId.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }*/
}

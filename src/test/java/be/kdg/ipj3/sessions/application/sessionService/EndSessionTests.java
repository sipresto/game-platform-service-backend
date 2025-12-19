package be.kdg.ipj3.sessions.application.sessionService;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.sessions.application.SessionService;
import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.domain.SessionRepository;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.sessions.shared.SessionStatus;
import be.kdg.ipj3.users.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class EndSessionTests {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
    }

    private Session createTestSession(UserId userId, boolean isFinished) {
        Session session = new Session(
                SessionId.create(),
                GameId.create(),
                List.of(userId),
                Difficulty.EASY
        );

        if (isFinished) {
            session.end(SessionStatus.PLAYER_WIN, userId, Instant.now());
        }

        sessionRepository.save(session);
        return session;
    }

    @Test
    void endSession_shouldEndSession_whenSessionExists() {
        // Arrange
        UserId userId = UserId.create();
        Session session = createTestSession(userId, false);

        // Act
        sessionService.endSession(session.getId(), SessionStatus.PLAYER_WIN, userId, Instant.now());

        // Assert
        Optional<Session> dbSession = sessionService.findById(session.getId());

        assertThat(dbSession).isPresent();

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Session actualSession = dbSession.get();
        assertThat(actualSession.getWinner()).isEqualTo(userId);
    }

    @Test
    void endSession_shouldNotUpdate_whenSessionAlreadyEnded() {
        // Arrange
        UserId userId = UserId.create();
        Session session = createTestSession(userId, true);

        // Act
        sessionService.endSession(session.getId(), SessionStatus.AI_WIN, null, Instant.now());

        // Assert
        Optional<Session> dbSession = sessionService.findById(session.getId());

        assertThat(dbSession).isPresent();

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Session actualSession = dbSession.get();
        assertThat(actualSession.getWinner()).isEqualTo(userId);
    }

    @Test
    void endSession_shouldNotCrash_whenSessionDoesNotExist() {
        // Act
        SessionId sessionId = SessionId.create();
        sessionService.endSession(sessionId, SessionStatus.AI_WIN, null, Instant.now());

        // Assert
        Optional<Session> dbSession = sessionService.findById(sessionId);

        assertThat(dbSession).isNotPresent();
    }

    @Test
    void endSession_shouldSetNoWinner_whenWinnerIdNotInPlayers() {
        // Arrange
        UserId userId = UserId.create();
        Session session = createTestSession(userId, false);

        // Act
        sessionService.endSession(session.getId(), SessionStatus.PLAYER_WIN, UserId.create(), Instant.now());

        // Assert
        Optional<Session> dbSession = sessionService.findById(session.getId());

        assertThat(dbSession).isPresent();

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Session actualSession = dbSession.get();
        assertThat(actualSession.getWinner()).isEqualTo(null);
    }
}

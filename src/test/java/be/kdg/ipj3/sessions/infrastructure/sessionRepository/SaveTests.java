package be.kdg.ipj3.sessions.infrastructure.sessionRepository;

import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.sessions.domain.session.Session;
import be.kdg.ipj3.sessions.domain.session.SessionId;
import be.kdg.ipj3.sessions.domain.session.SessionResult;
import be.kdg.ipj3.sessions.infrastructure.SessionRepository;
import be.kdg.ipj3.sessions.infrastructure.jpa.JpaSessionEntity;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(SessionRepository.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class SaveTests {

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void save_shouldPersistSession_whenSessionIsValid() {
        // Arrange
        SessionId sessionId = SessionId.create();
        GameId gameId = GameId.create();
        UserId player1 = UserId.create();
        Instant completedAt = Instant.now();

        Session newSession = new Session(
                sessionId,
                gameId,
                List.of(player1),
                Difficulty.HARD,
                player1,
                SessionResult.PLAYER_WIN,
                completedAt
        );

        // Act
        sessionRepository.save(newSession);

        // Assert
        JpaSessionEntity foundSession = entityManager.find(JpaSessionEntity.class, sessionId.id());
        assertThat(foundSession).isNotNull();
        assertThat(foundSession.getGameId()).isEqualTo(gameId.id());
        assertThat(foundSession.getPlayers().size()).isEqualTo(1);
        assertThat(foundSession.getPlayers().getFirst()).isEqualTo(player1.id());
        assertThat(foundSession.getDifficulty()).isEqualTo(Difficulty.HARD);
        assertThat(foundSession.getWinnerId()).isEqualTo(player1.id());
        assertThat(foundSession.getResult()).isEqualTo(SessionResult.PLAYER_WIN);
        assertThat(foundSession.getCompletedAt()).isEqualTo(completedAt);
    }

    @Test
    void save_shouldUpdateSession_whenSessionAlreadyExists() {
        // Arrange
        SessionId sessionId = SessionId.create();
        GameId gameId = GameId.create();
        UserId player1 = UserId.create();
        UserId player2 = UserId.create();
        Instant originalTime = Instant.now().minusSeconds(3600);
        Instant updatedTime = Instant.now();

        Session originalSession = new Session(
                sessionId,
                gameId,
                List.of(player1, player2),
                null,
                player1,
                SessionResult.PLAYER_WIN,
                originalTime
        );
        sessionRepository.save(originalSession);

        Session updatedSession = new Session(
                sessionId,
                gameId,
                List.of(player1),
                Difficulty.HARD,
                null,
                SessionResult.AI_WIN,
                updatedTime
        );

        // Act
        sessionRepository.save(updatedSession);

        // Assert
        JpaSessionEntity foundSession = entityManager.find(JpaSessionEntity.class, sessionId.id());
        assertThat(foundSession).isNotNull();
        assertThat(foundSession.getPlayers().size()).isEqualTo(1);
        assertThat(foundSession.getDifficulty()).isEqualTo(Difficulty.HARD);
        assertThat(foundSession.getWinnerId()).isEqualTo(null);
        assertThat(foundSession.getResult()).isEqualTo(SessionResult.AI_WIN);
        assertThat(foundSession.getCompletedAt()).isEqualTo(updatedTime);
    }
}
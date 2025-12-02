package be.kdg.ipj3.sessions.infrastructure.sessionRepository;

import be.kdg.ipj3.lobbies.domain.lobby.Difficulty;
import be.kdg.ipj3.sessions.domain.session.Session;
import be.kdg.ipj3.sessions.domain.session.SessionResult;
import be.kdg.ipj3.sessions.infrastructure.SessionRepository;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(SessionRepository.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class FindSessionsByUserIdTests {

    @Autowired
    SessionRepository sessionRepository;

    private void createTestSession(UserId userId) {
        Session session = new Session(
                GameId.create(),
                List.of(userId),
                Difficulty.HARD,
                null,
                SessionResult.AI_WIN
        );
        sessionRepository.save(session);

    }

    @Test
    void findSessionsByUserId_shouldReturnAllSessions_whenUserHasSessions() {
        // Arrange
        UserId user1 = UserId.create();
        UserId user2 = UserId.create();

        createTestSession(user1);
        createTestSession(user1);
        createTestSession(user2);

        // Act
        List<Session> result = sessionRepository.findSessionsByUserId(user1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.stream().allMatch(s -> s.getPlayers().contains(user1))).isTrue();
    }

    @Test
    void findSessionsByUserId_shouldReturnEmptyList_whenUserHasNoSessions() {
        // Arrange
        UserId user1 = UserId.create();
        UserId user2 = UserId.create();

        createTestSession(user1);

        // Act
        List<Session> result = sessionRepository.findSessionsByUserId(user2);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
    }
}

package be.kdg.ipj3.sessions.application;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.lobbies.shared.Difficulty;
import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.domain.SessionRepository;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.sessions.shared.SessionStatus;
import be.kdg.ipj3.sessions.shared.SessionsApi;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SessionService implements SessionsApi {
    private final SessionRepository sessionRepository;
    private final UsersApi usersApi;

    public SessionService(SessionRepository sessionRepository, UsersApi usersApi) {
        this.sessionRepository = sessionRepository;
        this.usersApi = usersApi;
    }

    @Override
    public void createSession(UUID id, UUID gameId, List<UUID> players, Difficulty difficulty) {
        List<UserId> playerIds = players.stream()
                .map(UserId::new)
                .toList();
        Session session = new Session(new SessionId(id), new GameId(gameId), playerIds, difficulty);

        sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<Session> findFinishedSessionsByUserId(String keycloakId) {
        UserId userId = usersApi.findIdByKeycloakId(keycloakId);

        return sessionRepository.findFinishedSessionsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public GameId findGameIdBySessionId(UUID id) {
        SessionId sessionId = new SessionId(id);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(sessionId::notFound);

        return session.getGame();
    }

    public void endSession(SessionId id, SessionStatus status, UserId winnerId, Instant completedAt) {
        Optional<Session> optionalSession = sessionRepository.findById(id);
        if (optionalSession.isEmpty()) {
            return;
        }

        Session session = optionalSession.get();

        session.end(status, winnerId, completedAt);

        sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public Optional<Session> findById(SessionId id) {
        return sessionRepository.findById(id);
    }
}

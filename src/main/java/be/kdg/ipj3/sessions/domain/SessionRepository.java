package be.kdg.ipj3.sessions.domain;

import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.users.shared.UserId;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    void save(Session session);

    List<Session> findFinishedSessionsByUserId(UserId userId);

    void deleteAll();

    Optional<Session> findById(SessionId sessionId);

    List<Session> findAllSessions();
}

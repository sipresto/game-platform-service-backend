package be.kdg.ipj3.sessions.domain.session;

import be.kdg.ipj3.shared.domain.UserId;

import java.util.List;

public interface ISessionRepository {
    void save(Session session);

    List<Session> findSessionsByUserId(UserId userId);
}

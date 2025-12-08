package be.kdg.ipj3.sessions.domain;

import be.kdg.ipj3.users.shared.UserId;

import java.util.List;

public interface SessionRepository {
    void save(Session session);

    List<Session> findSessionsByUserId(UserId userId);

    void deleteAll();
}

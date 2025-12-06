package be.kdg.ipj3.sessions.application;

import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.domain.SessionRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public List<Session> findSessionsByUserId(UserId userId) {
        return sessionRepository.findSessionsByUserId(userId);
    }

    public void save(Session session) {
        sessionRepository.save(session);
    }
}

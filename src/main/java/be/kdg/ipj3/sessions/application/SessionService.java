package be.kdg.ipj3.sessions.application;

import be.kdg.ipj3.sessions.domain.session.ISessionRepository;
import be.kdg.ipj3.sessions.domain.session.Session;
import be.kdg.ipj3.shared.domain.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SessionService {
    private final ISessionRepository sessionRepository;

    public SessionService(ISessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public List<Session> findSessionsByUserId(UserId userId) {
        return sessionRepository.findSessionsByUserId(userId);
    }

    public void save(Session session) {
        sessionRepository.save(session);
    }
}

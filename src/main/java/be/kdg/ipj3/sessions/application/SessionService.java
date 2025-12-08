package be.kdg.ipj3.sessions.application;

import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.domain.SessionRepository;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UsersApi usersApi;

    public SessionService(SessionRepository sessionRepository, UsersApi usersApi) {
        this.sessionRepository = sessionRepository;
        this.usersApi = usersApi;
    }

    public List<Session> findSessionsByUserId(String keycloakId) {
        UserId userId = usersApi.findIdByKeycloakId(keycloakId);

        return sessionRepository.findSessionsByUserId(userId);
    }

    public void save(Session session) {
        sessionRepository.save(session);
    }
}

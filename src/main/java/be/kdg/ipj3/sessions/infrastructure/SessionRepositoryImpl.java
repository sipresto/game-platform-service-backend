package be.kdg.ipj3.sessions.infrastructure;

import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.domain.SessionRepository;
import be.kdg.ipj3.sessions.infrastructure.jpa.JpaSessionEntity;
import be.kdg.ipj3.sessions.infrastructure.jpa.JpaSessionRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SessionRepositoryImpl implements SessionRepository {
    private final JpaSessionRepository jpaSessionRepository;

    public SessionRepositoryImpl(JpaSessionRepository jpaSessionRepository) {
        this.jpaSessionRepository = jpaSessionRepository;
    }

    @Override
    public void save(Session session) {
        jpaSessionRepository.save(JpaSessionEntity.fromDomain(session));
    }

    @Override
    public List<Session> findSessionsByUserId(UserId userId) {
        return jpaSessionRepository.findSessionsByUserId(userId.id()).stream()
                .map(JpaSessionEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAll() {
        jpaSessionRepository.deleteAll();
    }
}

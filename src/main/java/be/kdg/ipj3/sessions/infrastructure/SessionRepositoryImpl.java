package be.kdg.ipj3.sessions.infrastructure;

import be.kdg.ipj3.sessions.domain.Session;
import be.kdg.ipj3.sessions.domain.SessionRepository;
import be.kdg.ipj3.sessions.infrastructure.jpa.JpaSessionEntity;
import be.kdg.ipj3.sessions.infrastructure.jpa.JpaSessionRepository;
import be.kdg.ipj3.sessions.shared.SessionId;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public List<Session> findFinishedSessionsByUserId(UserId userId) {
        return jpaSessionRepository.findFinishedSessionsByUserId(userId.id()).stream()
                .map(JpaSessionEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAll() {
        jpaSessionRepository.deleteAll();
    }

    @Override
    public Optional<Session> findById(SessionId id) {
        return jpaSessionRepository.findById(id.id()).map(JpaSessionEntity::toDomain);
    }

    @Override
    public List<Session> findAllSessions() {
        return jpaSessionRepository.findAll().stream()
                .map(JpaSessionEntity::toDomain)
                .toList();
    }
}

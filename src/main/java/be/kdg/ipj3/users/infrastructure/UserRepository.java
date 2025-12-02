package be.kdg.ipj3.users.infrastructure;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository {
    private final JpaUserRepository jpaUserRepository;

    public UserRepository(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return jpaUserRepository.findById(userId.id())
                .map(JpaUserEntity::toDomain);
    }

    @Override
    public void save(User user) {
        jpaUserRepository.save(JpaUserEntity.fromDomain(user));
    }

    @Override
    public Optional<User> findByKeycloakId(String keycloakId) {
        return jpaUserRepository.findByKeycloakId(keycloakId)
                .map(JpaUserEntity::toDomain);
    }

    @Override
    public List<GameId> findOwnedGamesByKeycloakId(String keycloakId) {
        return jpaUserRepository.findOwnedGamesByKeycloakId(keycloakId).stream()
                .map(GameId::new)
                .toList();
    }

    @Override
    public void deleteAll() {
        jpaUserRepository.deleteAll();
    }

    @Override
    public List<User> findAllPublicUsers() {
        return jpaUserRepository.findAllByVisibility(Visibility.PUBLIC).stream().map(JpaUserEntity::toDomain).toList();
    }

    @Override
    public List<User> findFriends(UserId userId) {
        return jpaUserRepository.findFriends(userId.id()).stream().map(JpaUserEntity::toDomain).toList();
    }
}

package be.kdg.ipj3.users.infrastructure;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.domain.UserRepository;
import be.kdg.ipj3.users.domain.Visibility;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserEntity;
import be.kdg.ipj3.users.infrastructure.jpa.JpaUserRepository;
import be.kdg.ipj3.users.shared.UserId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return jpaUserRepository.findById(userId.id())
                .map(JpaUserEntity::toDomain);
    }

    public Optional<User> findByIdForUpdate(UserId userId) {
        return jpaUserRepository.findByIdForUpdate(userId.id())
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
    public List<GameId> findOwnedGamesById(UserId id) {
        return jpaUserRepository.findOwnedGamesById(id.id()).stream()
                .map(GameId::new)
                .toList();
    }

    @Override
    public List<GameId> findFavoriteGamesByUserId(UserId id){
        return jpaUserRepository.findFavoriteGamesByUserId(id.id())
                .stream()
                .map(GameId::new).toList();
    }
    @Override
    public void deleteAll() {
        jpaUserRepository.deleteAll();
    }

    @Override
    public List<User> findAllPublicUsers() {
        var publicUsers = jpaUserRepository.findAllByVisibility(Visibility.PUBLIC);
        return publicUsers.stream().map(JpaUserEntity::toDomain).toList();
    }

    @Override
    public List<User> findFriends(UserId id) {
        var friends = jpaUserRepository.findFriends(id.id());
        return friends.stream().map(JpaUserEntity::toDomain).toList();
    }

    @Override
    public boolean existsById(UserId id) {
        return jpaUserRepository.existsById(id.id());
    }
}

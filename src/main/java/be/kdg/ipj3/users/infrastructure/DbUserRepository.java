package be.kdg.ipj3.users.infrastructure;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.infrastructure.jpa.IUserJpaRepository;
import be.kdg.ipj3.users.infrastructure.jpa.UserJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class DbUserRepository implements IUserRepository {
    private final IUserJpaRepository userJpaRepository;

    public DbUserRepository(IUserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }


    @Override
    public Optional<User> getUserByIdWithGames(UserId userId) {
        return userJpaRepository.findByIdWithGames(userId.value()).map(UserJpaEntity::toDomain);
    }
}

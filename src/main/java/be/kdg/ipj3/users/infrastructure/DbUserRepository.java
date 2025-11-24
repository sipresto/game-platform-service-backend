package be.kdg.ipj3.users.infrastructure;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.infrastructure.jpa.*;
import be.kdg.ipj3.users.infrastructure.jpa.IUserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DbUserRepository implements IUserRepository {
    private final IUserJpaRepository userRepo;
    private final JpaProfileRepository profileRepo;

    public DbUserRepository(IUserJpaRepository userRepo, JpaProfileRepository profileRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userRepo.findById(userId.value()).map(JpaUserEntity::toDomain);
    }

    @Override
    public User save(User user) {
        return userRepo.save(JpaUserEntity.fromDomain(user)).toDomain();
    }

    @Override
    public boolean existsById(UserId userId) {
        return userRepo.existsById(userId.value());
    }

    @Override
    public Optional<User> getUserByIdWithGames(UserId userId) {
        return userRepo.findByIdWithGames(userId.value()).map(JpaUserEntity::toDomain);
    }

    public Optional<Profile> findProfileByUserId(UserId userId) {
        return profileRepo.findById(userId.value())
                .map(JpaProfileEntity::toDomain);
    }

    @Override
    public Profile save(Profile profile) {
        profileRepo.save(JpaProfileEntity.fromDomain(profile));
        return profile;
    }
}

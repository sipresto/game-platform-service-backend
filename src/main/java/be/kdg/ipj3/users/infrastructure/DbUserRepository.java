package be.kdg.ipj3.users.infrastructure;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.infrastructure.jpa.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DbUserRepository implements IUserRepository {
    private final JpaUserRepository userRepo;
    private final JpaProfileRepository profileRepo;

    public DbUserRepository(JpaUserRepository userRepo, JpaProfileRepository profileRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userRepo.findById(userId.value())
                .map(JpaUserEntity::toDomain);
    }

    @Override
    public User save(User user) {
        userRepo.save(JpaUserEntity.fromDomain(user));
        return user;
    }

    @Override
    public boolean existsById(UserId userId) {
        return userRepo.existsById(userId.value());
    }

    @Override
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
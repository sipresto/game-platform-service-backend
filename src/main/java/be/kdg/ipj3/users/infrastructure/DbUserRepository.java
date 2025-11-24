package be.kdg.ipj3.users.infrastructure;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.IUserRepository;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.infrastructure.jpa.*;
import be.kdg.ipj3.users.infrastructure.jpa.IUserJpaRepository;
import be.kdg.ipj3.users.infrastructure.jpa.UserJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class DbUserRepository implements IUserRepository {
    private final JpaUserRepository userRepo;
    private final JpaProfileRepository profileRepo;

    public DbUserRepository(JpaUserRepository userRepo, JpaProfileRepository profileRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }
    private final IUserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(UserId userId) {
        return userRepo.findById(userId.value())
                .map(JpaUserEntity::toDomain);
    public DbUserRepository(IUserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
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
    public Optional<User> getUserByIdWithGames(UserId userId) {
        return userJpaRepository.findByIdWithGames(userId.value()).map(UserJpaEntity::toDomain);
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

package be.kdg.ipj3.users.infrastructure.jpa;

import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.users.domain.Profile;
import be.kdg.ipj3.users.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId userId);
    User save(User user);
    boolean existsById(UserId userId);
    Optional<Profile> findProfileByUserId(UserId userId);
    Profile save(Profile profile);
}
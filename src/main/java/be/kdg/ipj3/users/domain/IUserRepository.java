package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.UserId;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findById(UserId userId);
    User save(User user);
    boolean existsById(UserId userId);
    Optional<Profile> findProfileByUserId(UserId userId);
    Optional<User> getUserByIdWithGames(UserId userId);
    Profile save(Profile profile);
}
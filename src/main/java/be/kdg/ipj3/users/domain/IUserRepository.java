package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.UserId;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findById(UserId userId);

    void save(User user);

    boolean existsById(UserId userId);

    Optional<User> findByIdWithGames(UserId userId);

    Optional<User> findByKeycloakId(String keycloakId);
}
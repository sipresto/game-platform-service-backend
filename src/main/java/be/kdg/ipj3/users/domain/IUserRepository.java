package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    Optional<User> findById(UserId userId);

    void save(User user);

    Optional<User> findByKeycloakId(String keycloakId);

    List<GameId> findOwnedGamesByKeycloakId(String keycloakId);

    void deleteAll();
}
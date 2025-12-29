package be.kdg.ipj3.users.domain;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.shared.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId userId);

    Optional<User> findByIdForUpdate(UserId userId);

    void save(User user);

    Optional<User> findByKeycloakId(String keycloakId);

    List<GameId> findOwnedGamesById(UserId id);

    List<GameId> findFavoriteGamesByUserId(UserId id);

    void deleteAll();

    List<User> findAll();

    List<User> findFriends(UserId userId);

    boolean existsById(UserId id);
}
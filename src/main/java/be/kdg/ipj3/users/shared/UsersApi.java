package be.kdg.ipj3.users.shared;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.domain.User;
import org.springframework.modulith.NamedInterface;

@NamedInterface
public interface UsersApi {
    User findByKeycloakId(String keycloakId);

    User registerOrGetUser(String keycloakId, String keycloakUsername);

    void addGame(UserId id, GameId gameId);

    User findById(UserId id);

    boolean existsById(UserId id);
}

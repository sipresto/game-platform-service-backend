package be.kdg.ipj3.users.shared;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.users.domain.User;
import org.springframework.modulith.NamedInterface;

import java.util.List;
import java.util.UUID;

@NamedInterface
public interface UsersApi {
    User findByKeycloakId(String keycloakId);

    User registerOrGetUser(String keycloakId, String keycloakUsername, String email);

    void addGame(UUID id, UUID gameId);

    User findById(UUID id);

    boolean existsById(UUID id);

    UserId findIdByKeycloakId(String keycloakId);

    List<GameId> findOwnedGamesById(UUID id);

    List<GameId> findFavoriteGamesByUserId(UUID id);

    void addPoints(UUID id, int points);

    SmallUserDto findSmallUserDtoById(UUID id);

    String findEmailByUserId(UserId id);
}

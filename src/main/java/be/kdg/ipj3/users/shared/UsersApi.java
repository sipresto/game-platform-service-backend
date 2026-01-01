package be.kdg.ipj3.users.shared;

import be.kdg.ipj3.catalog.shared.GameId;
import org.springframework.modulith.NamedInterface;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@NamedInterface
public interface UsersApi {

    void addGame(UUID id, UUID gameId);

    void addFavorite(String id, UUID gameId);

    void removeFavorite(String id, UUID gameId);

    boolean existsById(UUID id);

    UserId findIdByKeycloakId(String keycloakId);

    List<GameId> findOwnedGamesById(UUID id, String viewerKeycloakId);

    List<GameId> findFavoriteGamesByUserId(UUID id);

    void addPoints(UUID id, int points);

    SmallUserDto findSmallUserDtoById(UUID id);

    EmailUserDto findEmailUserDtoByKeycloakId(UUID id);

    String findEmailByUserId(UserId id);

    String findDisplayNameById(UserId id);

    boolean checkBlock(Set<UserId> participantIds);

    void purchasePerk(UUID perkId, int pointCost,UserId userId);
    void equipTitle(UserId userId, UUID perkId);
    void equipTheme(UserId userId, UUID perkId);
    void equipBorder(UserId userId, UUID perkId);

    void unequipBorder(UserId userId);
    void unequipTheme(UserId userId);
    void unequipTitle(UserId userId);

    UserCosmeticsDto getUserCosmetics(UserId userId);
}

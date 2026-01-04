package be.kdg.ipj3.shop.application;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.shared.domain.exceptions.UserNotFoundException;
import be.kdg.ipj3.shop.domain.IPurchaseRepository;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PurchaseService {
    private final IPurchaseRepository purchaseRepository;
    private final UsersApi usersApi;
    private final GamesApi gamesApi;

    public PurchaseService(IPurchaseRepository purchaseRepository, UsersApi usersApi, GamesApi gamesApi) {
        this.purchaseRepository = purchaseRepository;
        this.usersApi = usersApi;
        this.gamesApi = gamesApi;
    }

    public Purchase buy(String keycloakId, UUID gameIdParam, String chargeId) {
        GameId gameId = new GameId(gameIdParam);

        UserId userId = usersApi.findIdByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));

        if (!gamesApi.existsById(gameIdParam)) {
            throw GameId.notFound(gameIdParam);
        }

        usersApi.addGame(userId.id(), gameIdParam);

        Purchase purchase = new Purchase(userId, gameId, chargeId);
        return purchaseRepository.save(purchase);
    }
}

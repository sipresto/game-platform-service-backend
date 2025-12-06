package be.kdg.ipj3.shop.application;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.shop.domain.IPurchaseRepository;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.shared.UserId;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PurchaseService {
    private final IPurchaseRepository purchaseRepository;
    private final GamesApi gamesApi;
    private final UsersApi usersApi;

    public PurchaseService(IPurchaseRepository purchaseRepository, GamesApi gamesApi, UsersApi usersApi) {
        this.purchaseRepository = purchaseRepository;
        this.gamesApi = gamesApi;
        this.usersApi = usersApi;
    }

    public Purchase buy(String userId, UUID gameIdParam, String chargeId) {
        gamesApi.existsById(gameIdParam);
        GameId gameId = new GameId(gameIdParam);
        User user = usersApi.findByKeycloakId(userId);

        usersApi.addGame(user.getId(), gameId);

        Purchase purchase = new Purchase(user.getId(), gameId, chargeId);
        return purchaseRepository.save(purchase);
    }
}

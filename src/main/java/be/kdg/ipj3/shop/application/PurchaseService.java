package be.kdg.ipj3.shop.application;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shop.domain.IPurchaseRepository;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.users.domain.User;
import be.kdg.ipj3.users.shared.UsersApi;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PurchaseService {
    private final IPurchaseRepository purchaseRepository;
    private final UsersApi usersApi;

    public PurchaseService(IPurchaseRepository purchaseRepository, UsersApi usersApi) {
        this.purchaseRepository = purchaseRepository;
        this.usersApi = usersApi;
    }

    public Purchase buy(String userId, UUID gameIdParam, String chargeId) {
        GameId gameId = new GameId(gameIdParam);

        User user = usersApi.findByKeycloakId(userId);

        usersApi.addGame(user.getId().id(), gameIdParam);

        Purchase purchase = new Purchase(user.getId(), gameId, chargeId);
        return purchaseRepository.save(purchase);
    }
}

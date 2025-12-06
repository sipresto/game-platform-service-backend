package be.kdg.ipj3.shop.application;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.shop.domain.IPurchaseRepository;
import be.kdg.ipj3.shop.domain.Purchase;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService {
    private final IPurchaseRepository purchaseRepository;

    public PurchaseService(IPurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public Purchase buy(UserId userId, GameId gameId, String chargeId) {
        Purchase purchase = new Purchase(userId, gameId,chargeId);
        return purchaseRepository.save(purchase);
    }
}

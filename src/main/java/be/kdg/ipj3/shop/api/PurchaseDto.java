package be.kdg.ipj3.shop.api;

import be.kdg.ipj3.shop.domain.Purchase;

import java.time.Instant;
import java.util.UUID;

public record PurchaseDto(UUID id, UUID buyer, UUID game, Instant purchasedAt) {
    public static PurchaseDto fromDomain(Purchase purchase) {
        return new PurchaseDto(
                purchase.getId().id(),
                purchase.getBuyer().id(),
                purchase.getGame().id(),
                purchase.getPurchasedAt()
        );
    }
}

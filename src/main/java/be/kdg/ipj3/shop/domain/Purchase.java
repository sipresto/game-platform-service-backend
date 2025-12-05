package be.kdg.ipj3.shop.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;

@AggregateRoot
public record Purchase(
        @Identity PurchaseId id,
        UserId buyer,
        GameId game,
        Instant purchasedAt,
        String chargeId

        ) {

    public Purchase(UserId buyer, GameId game, String chargeId) {
        this(PurchaseId.create(), buyer, game, Instant.now(),chargeId);
    }

    @Override
    public PurchaseId id() {
        return id;
    }

}

package be.kdg.ipj3.shop.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;

@AggregateRoot
public record Purchase(@Identity PurchaseId id, UserId buyer, GameId game, Instant purchasedAt) {

    public Purchase(UserId buyer, GameId game) {
        this(PurchaseId.create(), buyer, game, Instant.now());
    }

    @Override
    public PurchaseId id() {
        return id;
    }
}

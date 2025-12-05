package be.kdg.ipj3.shop.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;

@AggregateRoot
public class Purchase {
    @Identity
    private final PurchaseId id;
    private final UserId buyer;
    private final GameId game;
    private final Instant purchasedAt;

    public Purchase(PurchaseId id, UserId buyer, GameId game, Instant purchasedAt) {
        this.id  = id;
        this.buyer = buyer;
        this.game = game;
        this.purchasedAt = purchasedAt;
    }

    public Purchase(UserId buyer, GameId game) {
        this(PurchaseId.create(), buyer, game, Instant.now());
    }

    public PurchaseId getId() {
        return id;
    }

    public UserId getBuyer() {
        return buyer;
    }

    public GameId getGame() {
        return game;
    }

    public Instant getPurchasedAt() {
        return purchasedAt;
    }
}

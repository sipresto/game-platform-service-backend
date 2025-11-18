package be.kdg.ipj3.shop.domain;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.List;

@AggregateRoot
public class Purchase {
    @Identity
    private final PurchaseId id;
    private final UserId buyer;
    private final List<GameId> games;
    private final Instant purchasedAt;

    public Purchase(PurchaseId id, UserId buyer, List<GameId> games, Instant purchasedAt) {
        this.id  = id;
        this.buyer = buyer;
        this.games = games;
        this.purchasedAt = purchasedAt;
    }

    public Purchase(UserId buyer, List<GameId> games) {
        this(PurchaseId.create(), buyer, games, Instant.now());
    }

    public PurchaseId getId() {
        return id;
    }

    public UserId getBuyer() {
        return buyer;
    }

    public List<GameId> getGames() {
        return games;
    }

    public Instant getPurchasedAt() {
        return purchasedAt;
    }
}

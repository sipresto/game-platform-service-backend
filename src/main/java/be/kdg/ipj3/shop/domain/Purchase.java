package be.kdg.ipj3.shop.domain;

import be.kdg.ipj3.shared.domain.game.GameId;
import be.kdg.ipj3.shared.domain.user.UserId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AggregateRoot
public class Purchase {
    @Identity
    private final PurchaseId purchaseId;
    private final List<GameId> gameIds;
    private final UserId owner;
    private final Instant purchasedAt;

    public Purchase(UserId owner, List<GameId> gameIds) {
        this.owner = owner;
        this.gameIds = gameIds;
        this.purchaseId = new PurchaseId(UUID.randomUUID());
        this.purchasedAt = Instant.now();
    }

    public PurchaseId purchaseId() {
        return purchaseId;
    }

    public List<GameId> gameIds() {
        return gameIds;
    }

    public UserId owner() {
        return owner;
    }

    public Instant purchasedAt() {
        return purchasedAt;
    }
}

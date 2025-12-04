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
        String paymentId,
        PaymentStatus status
        ) {

    public Purchase(UserId buyer, GameId game, String paymentId) {
        this(PurchaseId.create(), buyer, game, Instant.now(), paymentId, PaymentStatus.PENDING);
    }

    @Override
    public PurchaseId id() {
        return id;
    }

    public Purchase markAsPaid() {
        return new Purchase(this.id, this.buyer, this.game, this.purchasedAt, this.paymentId, PaymentStatus.PAID);
    }
}

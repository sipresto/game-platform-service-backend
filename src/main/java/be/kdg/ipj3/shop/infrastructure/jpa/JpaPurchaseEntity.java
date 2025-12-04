package be.kdg.ipj3.shop.infrastructure.jpa;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.shop.domain.PaymentStatus;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.shop.domain.PurchaseId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchases")
public class JpaPurchaseEntity {
    @Id
    @Column(name = "purchase_id")
    private UUID id;

    @Column(nullable = false)
    private UUID buyer;

    @Column(nullable = false)
    private UUID game;

    @Column(nullable = false)
    private Instant purchasedAt;
    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String status;

    protected JpaPurchaseEntity() {} // for JPA

    public JpaPurchaseEntity(UUID id, UUID buyer, UUID game, Instant purchasedAt, String paymentId, String status) {
        this.id = id;
        this.buyer = buyer;
        this.game = game;
        this.purchasedAt = purchasedAt;
        this.paymentId = paymentId;
        this.status = status;
    }

    public static JpaPurchaseEntity fromDomain(Purchase purchase) {
        return new JpaPurchaseEntity(
                purchase.id().id(),
                purchase.buyer().id(),
                purchase.game().id(),
                purchase.purchasedAt(),
                purchase.paymentId(),
                purchase.status().name()
        );
    }

    public Purchase toDomain() {
        return new Purchase(
                new PurchaseId(this.id),
                new UserId(this.buyer),
                new GameId(this.game),
                this.purchasedAt,
                this.paymentId,
                PaymentStatus.valueOf(this.status)
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getBuyer() {
        return buyer;
    }

    public UUID getGame() {
        return game;
    }

    public Instant getPurchasedAt() {
        return purchasedAt;
    }
}

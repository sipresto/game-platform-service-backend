package be.kdg.ipj3.shop.infrastructure.jpa;

import be.kdg.ipj3.catalog.shared.GameId;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.shop.domain.PurchaseId;
import be.kdg.ipj3.users.shared.UserId;
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
    private String chargeId;

    protected JpaPurchaseEntity() {
    } // for JPA

    public JpaPurchaseEntity(UUID id, UUID buyer, UUID game, Instant purchasedAt, String chargeId) {
        this.id = id;
        this.buyer = buyer;
        this.game = game;
        this.purchasedAt = purchasedAt;
        this.chargeId = chargeId;
    }

    public static JpaPurchaseEntity fromDomain(Purchase purchase) {
        return new JpaPurchaseEntity(
                purchase.id().id(),
                purchase.buyer().id(),
                purchase.game().id(),
                purchase.purchasedAt(),
                purchase.chargeId()
        );
    }

    public Purchase toDomain() {
        return new Purchase(new PurchaseId(this.id), new UserId(this.buyer), new GameId(this.game), this.purchasedAt, this.chargeId);
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

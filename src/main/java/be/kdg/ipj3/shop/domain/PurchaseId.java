package be.kdg.ipj3.shop.domain;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public record PurchaseId(@NotNull UUID purchaseId) {
    public PurchaseId{
        Objects.requireNonNull(purchaseId, "purchaseId cannot be null.");
    }
}

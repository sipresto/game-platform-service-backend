package be.kdg.ipj3.shop.domain;

import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record PurchaseId(UUID id) {
    public PurchaseId {
        Objects.requireNonNull(id, "Purchase id cannot be null.");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Purchase [" + id + "] not found");
    }

    public static PurchaseId create() {
        return new PurchaseId(UUID.randomUUID());
    }
}

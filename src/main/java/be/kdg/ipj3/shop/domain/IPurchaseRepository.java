package be.kdg.ipj3.shop.domain;

import java.util.Optional;

public interface IPurchaseRepository {
    Purchase save(Purchase purchase);

    void deleteAll();

    Optional<Purchase> findByPaymentId(String paymentId);
}

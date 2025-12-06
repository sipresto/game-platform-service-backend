package be.kdg.ipj3.shop.infrastructure.stripe;

import java.util.Currency;

public record ChargeRequest(
        String description,
        int amount,
        Currency currency,
        String stripeToken
) {
}
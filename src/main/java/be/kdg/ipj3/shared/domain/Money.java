package be.kdg.ipj3.shared.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

@ValueObject
public record Money(Currency currency, BigDecimal amount) {
    public Money {
        Objects.requireNonNull(currency, "Currency cannot be nul");
        Objects.requireNonNull(amount, "Amount cannot be null");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(final BigDecimal amount) {
        return new Money(Currency.getInstance("EUR"), amount);
    }

    public static Money of(final Currency currency, final BigDecimal amount) {
        return new Money(currency, amount);
    }

    public Money add(final Money other) {
        Objects.requireNonNull(other, "Money to add cannot be null");
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add Money of different currencies");
        }
        BigDecimal result = amount.add(other.amount).setScale(2, RoundingMode.HALF_UP);
        return new Money(currency, result);
    }

    public Money subtract(final Money other) {
        Objects.requireNonNull(other, "Money to subtract cannot be null");
        if (!currency.equals(other.currency)){
            throw new IllegalArgumentException("Cannot subtract Money of different currencies");
        }
        BigDecimal result = amount.subtract(other.amount).setScale(2, RoundingMode.HALF_UP);
        return new Money(currency, result);
    }

}

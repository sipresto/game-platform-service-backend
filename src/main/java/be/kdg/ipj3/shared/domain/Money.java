package be.kdg.ipj3.shared.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;


@ValueObject
public record Money(Currency currency, BigDecimal balance) {
    public Money {
        Objects.requireNonNull(currency, "Gelieve de munteenheid op te geven.");
        Objects.requireNonNull(balance, "Gelieve de hoeveelheid geld op te geven.");
    }

    public static Money of(final int amount) {
        return new Money(Currency.getInstance("EUR"), BigDecimal.valueOf(amount));
    }

    public static Money of(final Currency currency, final int amount) {
        return new Money(currency, BigDecimal.valueOf(amount));
    }

    public Money add(final Money money) {
        return new Money(this.currency ,this.balance.add(money.balance));
    }

    public Money multiply(final int amount) {
        return new Money(this.currency ,this.balance.multiply(BigDecimal.valueOf(amount)));
    }
}

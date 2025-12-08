package be.kdg.ipj3.catalog.shared;

import be.kdg.ipj3.catalog.domain.Game;
import be.kdg.ipj3.shared.domain.Money;

public record GamePurchaseDto(
        String name,
        Money price
) {
    public static GamePurchaseDto fromDomain(Game game) {
        return new GamePurchaseDto(game.getName(), game.getPrice());
    }
}

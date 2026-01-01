package be.kdg.ipj3.shop.shared;

import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;

public class GameNotFoundException extends NotFoundException {
    public GameNotFoundException(String message) {
        super(message);
    }
}

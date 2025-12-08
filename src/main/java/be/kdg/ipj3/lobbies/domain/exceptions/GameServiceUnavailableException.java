package be.kdg.ipj3.lobbies.domain.exceptions;

public class GameServiceUnavailableException extends RuntimeException {
    public GameServiceUnavailableException(String message) {
        super(message);
    }
}

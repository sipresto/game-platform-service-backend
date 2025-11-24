package be.kdg.ipj3.sessions.domain.exception;

public class GameServiceUnavailableException extends RuntimeException {
    public GameServiceUnavailableException(String message) {
        super(message);
    }
}

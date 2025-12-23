package be.kdg.ipj3.lobbies.shared;

public class GameServiceUnavailableException extends RuntimeException {
    public GameServiceUnavailableException(String message) {
        super(message);
    }
}

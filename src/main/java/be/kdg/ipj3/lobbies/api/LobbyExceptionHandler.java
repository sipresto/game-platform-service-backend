package be.kdg.ipj3.lobbies.api;

import be.kdg.ipj3.lobbies.domain.exceptions.GameServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LobbyExceptionHandler {
    public record ErrorResponse(String message) {
    }

    @ExceptionHandler(GameServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> gameServiceUnavailableHandler(final GameServiceUnavailableException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
}

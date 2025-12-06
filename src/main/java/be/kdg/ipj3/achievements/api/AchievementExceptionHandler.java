package be.kdg.ipj3.achievements.api;

import be.kdg.ipj3.achievements.domain.exceptions.MetadataSerializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AchievementExceptionHandler {
    public record ErrorResponse(String message) {
    }

    @ExceptionHandler(MetadataSerializationException.class)
    public ResponseEntity<ErrorResponse> metadataSerializationExceptionHandler(final MetadataSerializationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

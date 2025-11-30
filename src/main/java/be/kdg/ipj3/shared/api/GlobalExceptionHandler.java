package be.kdg.ipj3.shared.api;

import be.kdg.ipj3.shared.domain.NotFoundException;
import be.kdg.ipj3.shared.domain.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldError(
                        fe.getField(),
                        String.valueOf(fe.getRejectedValue()),
                        fe.getDefaultMessage()
                ))
                .toList();

        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Input niet valide",
                fieldErrors,
                Instant.now()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid parameter '%s': value '%s' cannot be converted too %s",
                ex.getName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "onbekend type");

        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                List.of(),
                Instant.now()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleParseError(HttpMessageNotReadableException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Ongeldige JSON: " + ex.getMostSpecificCause().getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiError body = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        ApiError body = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid data",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex) {
        ApiError body = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Something went wrong: " + ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
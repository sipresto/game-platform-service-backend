package be.kdg.ipj3.shared.api;

import be.kdg.ipj3.users.shared.ServiceUnavailableException;
import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import be.kdg.ipj3.social.shared.InvalidChatRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ApiError> handleActiveGameNotFound(GameNotFoundException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Game wasn't found",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ApiError> handleEmailNotSent(EmailSendException ex) {
        ApiError body = new ApiError(
                HttpStatus.EXPECTATION_FAILED.value(),
                "No email could be sent",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(body);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiError> handleNotCorrectUserLoggedIn(SecurityException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid user",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid file",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiError> handleIllegalFile(IOException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid file",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

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

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        ApiError body = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Invalid state",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleGameServiceUnavailable(ServiceUnavailableException ex) {
        ApiError body = new ApiError(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(InvalidChatRequestException.class)
    public ResponseEntity<ApiError> handleInvalidChatRequest(InvalidChatRequestException ex) {
        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        ApiError body = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                List.of(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
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
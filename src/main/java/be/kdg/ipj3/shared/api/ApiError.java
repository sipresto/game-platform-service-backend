package be.kdg.ipj3.shared.api;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.List;

public record ApiError(
        int status,
        String error,
        String message,
        List<FieldError> fieldErrors,
        Instant timestamp
) {
    public record FieldError(
            String field,
            String rejectedValue,
            String reason
    ) {}
}

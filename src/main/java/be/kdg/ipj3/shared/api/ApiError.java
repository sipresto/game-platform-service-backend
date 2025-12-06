package be.kdg.ipj3.shared.api;

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
    ) {
    }
}

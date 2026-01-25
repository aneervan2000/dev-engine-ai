package com.anee.projects.lovable_clone.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/**
 * <h6>Represents an API error response.</h6>
 *
 * @param status: HTTP status of the error
 * @param message: Error message
 * @param timestamp: Time when the error occurred
 * @param errors: List of field-specific errors (optional) and included only if not null
 */

public record ApiError (
       HttpStatus status,
       String message,
       Instant timestamp,
       @JsonInclude(JsonInclude.Include.NON_NULL)
       List<APiFieldError> errors
) {
    public ApiError(HttpStatus status, String message) {
        this(status, message, Instant.now(), null);
    }

    public ApiError(HttpStatus status, String message, List<APiFieldError> errors) {
        this(status, message, Instant.now(), errors);
    }
}

/**
 * <h6>Represents a field-specific error in an API response.</h6>
 * @param field: The name of the field that caused the error
 * @param message: The error message associated with the field
 */
record APiFieldError (String field, String message) {}

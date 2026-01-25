package com.anee.projects.lovable_clone.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


/**
 * <h6>Global exception handler for REST controllers.</h6>
 * Catches specific exceptions and returns appropriate API error responses.
 *
 * Handles:
 * <ul>
 *  <li>BadRequestException: Returns 400 Bad Request</li>
 *  <li>ResourceNotFoundException: Returns 404 Not Found</li>
 *  <li>MethodArgumentNotValidException: Returns 400 Bad Request with field errors</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles BadRequestException and returns a 400 Bad Request response.
     *
     * @param ex  the BadRequestException instance
     * @return ResponseEntity containing the ApiError
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    /**
     * Handles ResourceNotFoundException and returns a 404 Not Found response.
     *
     * @param ex  the ResourceNotFoundException instance
     * @return ResponseEntity containing the ApiError
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getResourceName() + " with ID " + ex.getResourceId() + " not found.");
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    /**
     * Handles MethodArgumentNotValidException and returns a 400 Bad Request response
     * with detailed field errors.
     *
     * @param ex  the MethodArgumentNotValidException instance
     * @return ResponseEntity containing the ApiError with field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInputValidationError(MethodArgumentNotValidException ex) {

        List<APiFieldError> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new APiFieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Input validation failed", errors);
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }
}

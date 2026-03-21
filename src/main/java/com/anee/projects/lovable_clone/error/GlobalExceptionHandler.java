package com.anee.projects.lovable_clone.error;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
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

    /**
     * Handles UsernameNotFoundException and returns a 404 Not Found response.
     * @param ex the UsernameNotFoundException instance
     * @return ResponseEntity containing the ApiError
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "Username not found with username: " + ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    /**
     * Handles AuthenticationException and returns a 401 Unauthorized response.
     * @param ex the AuthenticationException instance
     * @return ResponseEntity containing the ApiError
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    /**
     * Handles JwtException and returns a 401 Unauthorized response indicating an invalid JWT token.
     * @param ex the JwtException instance
     * @return  ResponseEntity containing the ApiError
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    /**
     * Handles AccessDeniedException and returns a 403 Forbidden response indicating insufficient permissions.
     * @param ex the AccessDeniedException instance
     * @return ResponseEntity containing the ApiError
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Access denied: Insufficient permissions");
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }
}

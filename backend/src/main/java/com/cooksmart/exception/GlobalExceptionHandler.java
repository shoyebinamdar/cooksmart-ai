package com.cooksmart.exception;

import com.cooksmart.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() == null
                                ? "Invalid value"
                                : fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));

        log.warn("Validation failed for {}: {}", request.getRequestURI(), fieldErrors);
        return build(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Malformed JSON request or invalid enum value";
        Throwable cause = ex.getMostSpecificCause();
        if (cause != null && cause.getMessage() != null && !cause.getMessage().isBlank()) {
            message = cause.getMessage();
        }
        log.warn("Unreadable request for {}: {}", request.getRequestURI(), message);
        return build(HttpStatus.BAD_REQUEST, message, request.getRequestURI(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Bad request for {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(CookSmartException.class)
    public ResponseEntity<ErrorResponse> handleCookSmart(CookSmartException ex, HttpServletRequest request) {
        log.warn("Application error for {}: {}", request.getRequestURI(), ex.getMessage());
        return build(ex.getStatus(), ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error for {}", request.getRequestURI(), ex);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(),
                null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path, Object details) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(), status.value(), status.getReasonPhrase(), message, path, details);
        return ResponseEntity.status(status).body(body);
    }
}

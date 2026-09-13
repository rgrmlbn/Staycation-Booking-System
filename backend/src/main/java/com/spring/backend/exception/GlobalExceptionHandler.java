package com.spring.backend.exception;


import com.spring.backend.exception.common.ResourceNotFoundException;
import com.spring.backend.exception.property.property.DuplicateAmenityException;
import com.spring.backend.exception.property.property.DuplicateImageException;
import com.spring.backend.exception.property.property.InvalidTimeRangeException;
import com.spring.backend.exception.property.property.OverlappingTimeSlotException;
import com.spring.backend.exception.user.auth.InvalidTokenException;
import com.spring.backend.exception.user.auth.TokenExpiredException;
import com.spring.backend.exception.user.auth.TokenRevokedException;
import com.spring.backend.exception.user.auth.TooManyRequestsException;
import com.spring.backend.exception.user.user.DuplicateEmailException;
import com.spring.backend.exception.user.user.IncorrectCurrentPasswordException;
import com.spring.backend.exception.user.user.PasswordMismatchException;
import com.spring.backend.exception.user.user.PasswordReuseException;
import com.spring.backend.module.shared.response.ApiResponse;
import com.spring.backend.module.shared.response.ApiResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiResponseBuilder apiResponseBuilder;

    // Default

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        return apiResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // Common

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return apiResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // User

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        return apiResponseBuilder.error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IncorrectCurrentPasswordException.class)
    public ResponseEntity<ApiResponse> handleIncorrectCurrentPasswordException(IncorrectCurrentPasswordException ex) {
        return apiResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
        return apiResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PasswordReuseException.class)
    public ResponseEntity<ApiResponse> handlePasswordReuseException(PasswordReuseException ex) {
        return apiResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Auth

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidTokenException(InvalidTokenException ex) {
        return apiResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse> handleTokenExpiredException(TokenExpiredException ex) {
        return apiResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TokenRevokedException.class)
    public ResponseEntity<ApiResponse> handleTokenRevokedException(TokenRevokedException ex) {
        return apiResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiResponse> handleTooManyRequestsException(TooManyRequestsException ex) {
        return apiResponseBuilder.error(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    // Property

    @ExceptionHandler(DuplicateAmenityException.class)
    public ResponseEntity<ApiResponse> handleDuplicateAmenityException(DuplicateAmenityException ex) {
        return apiResponseBuilder.error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DuplicateImageException.class)
    public ResponseEntity<ApiResponse> handleDuplicateImageException(DuplicateImageException ex) {
        return apiResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidTimeRangeException.class)
    public ResponseEntity<ApiResponse> handleInvalidTimeRangeException(InvalidTimeRangeException ex) {
        return apiResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(OverlappingTimeSlotException .class)
    public ResponseEntity<ApiResponse> handleOverlappingTimeSlotException(OverlappingTimeSlotException ex) {
        return apiResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Built-in

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return apiResponseBuilder.error(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return apiResponseBuilder.error(HttpStatus.FORBIDDEN, "You do not have permission to access this resource.");
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        return apiResponseBuilder.error(HttpStatus.BAD_REQUEST, "Validation failed");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ApiResponse.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> ApiResponse.FieldError.builder()
                        .field(e.getField())
                        .message(e.getDefaultMessage())
                        .build())
                .toList();

        return apiResponseBuilder.validationError("Validation failed", errors);
    }

}
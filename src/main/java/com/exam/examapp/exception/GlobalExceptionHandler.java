package com.exam.examapp.exception;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.exception.custom.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        String message = ex.getMessage();
        log.error(message);
        return buildErrorResponse(HttpStatus.NOT_FOUND, message, null);
    }

    @ExceptionHandler(ReachedLimitException.class)
    public ResponseEntity<ApiResponse<Object>> handleReachedLimitException(
            ReachedLimitException ex, WebRequest request) {
        log.error(ex.getMessage());
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), null);
    }

    @ExceptionHandler(ExamExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleExamExpiredException(
            ExamExpiredException ex, WebRequest request) {
        log.error(ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), null);
    }

    @ExceptionHandler(DoesNotHavePermissionException.class)
    public ResponseEntity<ApiResponse<Object>> handleDoesNotHavePermissionException(
            DoesNotHavePermissionException ex, WebRequest request) {
        log.error(ex.getMessage());
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex) {
        log.warn("BadRequestException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(DirectoryException.class)
    public ResponseEntity<ApiResponse<Object>> handleDirectoryCreate(
            DirectoryException ex, WebRequest request) {
        log.error("{}: {}", "DirectoryException", ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ApiResponse<Object>> handleFileSave(FileException ex, WebRequest request) {
        log.error("{}: {}", "FileException", ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    @ExceptionHandler(UserNotLoginException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotLoginUser(
            UserNotLoginException ex, WebRequest request) {
        String message = ex.getMessage();
        log.error(message);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, message, null);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtException(
            JwtException ex, WebRequest request) {
        log.error(ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotLoginUser(
            InvalidCredentialsException ex, WebRequest request) {
        log.error("InvalidCredentialsException : {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        log.warn("MethodArgumentNotValidException: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST, "MethodArgumentNotValidException", errors);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceFound(NoResourceFoundException ex) {
        log.error("NoResourceFoundException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleHandlerMethodValidation(
            HandlerMethodValidationException ex) {
        log.warn("HandlerMethodValidationException: {}", ex.getMessage());
        List<String> errors =
                ex.getAllErrors().stream()
                        .map(
                                err -> {
                                    if (err instanceof FieldError fe) {
                                        return fe.getField() + ": " + fe.getDefaultMessage();
                                    }
                                    return err.getDefaultMessage();
                                })
                        .toList();

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "HandlerMethodValidationException", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(
            ConstraintViolationException ex) {
        log.warn("ConstraintViolationException: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations()
                .forEach(
                        cv -> {
                            String path = cv.getPropertyPath().toString();
                            String field = path.substring(path.lastIndexOf('.') + 1);
                            errors.put(field, cv.getMessage());
                        });

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "ConstraintViolationException", errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex) {
        log.warn("MissingServletRequestParameterException: {}", ex.getMessage());

        Map<String, String> errors =
                Map.of(ex.getParameterName(), "Missing parameter:");
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST, "MissingServletRequestParameterException", errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Exception: {} , Global exception: {}", ex.getMessage(), ex.getClass().getName());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    private ResponseEntity<ApiResponse<Object>> buildErrorResponse(
            HttpStatus status, String message, Object details) {
        return ResponseEntity.status(status).body(ApiResponse.build(status, message, details));
    }
}

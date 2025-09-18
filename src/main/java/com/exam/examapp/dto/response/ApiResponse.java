package com.exam.examapp.dto.response;

import java.time.Instant;
import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        Instant timestamp,
        int status,
        String message,
        T data) {

    public static <T> ApiResponse<T> build(HttpStatus status, String message, T data) {
        return new ApiResponse<>(Instant.now(), status.value(), message, data);
    }
}

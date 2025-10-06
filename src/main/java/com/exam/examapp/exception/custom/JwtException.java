package com.exam.examapp.exception.custom;

public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }
}

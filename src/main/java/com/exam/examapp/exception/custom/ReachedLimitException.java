package com.exam.examapp.exception.custom;

public class ReachedLimitException extends RuntimeException {
  public ReachedLimitException(String message) {
    super(message);
  }
}

package com.exam.examapp.exception.custom;

public class ExamExpiredException extends RuntimeException {
  public ExamExpiredException(String message) {
    super(message);
  }
}

package com.exam.examapp.exception.custom;

public class DoesNotHavePermissionException extends RuntimeException {
  public DoesNotHavePermissionException(String message) {
    super(message);
  }
}

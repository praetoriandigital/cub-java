package com.ivelum.exception;

public class NotFoundException extends ApiException {
  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

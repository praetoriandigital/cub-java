package com.ivelum.exception;

public class DeserializationException extends CubException {
  public DeserializationException(String message) {
    super(message);
  }

  public DeserializationException(String message, Throwable cause) {
    super(message, cause);
  }
}

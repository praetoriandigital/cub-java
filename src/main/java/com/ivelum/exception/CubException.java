package com.ivelum.exception;

public class CubException extends Exception {
  public CubException(String message) {
    super(message);
  }

  public CubException(String message, Throwable cause) {
    super(message, cause);
  }
}

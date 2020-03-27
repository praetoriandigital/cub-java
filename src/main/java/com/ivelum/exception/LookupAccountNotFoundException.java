package com.ivelum.exception;

public class LookupAccountNotFoundException extends ApiException {
  public LookupAccountNotFoundException(Throwable cause) {
    super("Account not found", cause);
  }
}

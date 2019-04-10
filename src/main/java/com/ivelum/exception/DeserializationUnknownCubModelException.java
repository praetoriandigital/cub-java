package com.ivelum.exception;

public class DeserializationUnknownCubModelException extends DeserializationException {
  public String unknownCubModelName;

  public DeserializationUnknownCubModelException(String name, String message) {
    super(message);
    this.unknownCubModelName = name;
  }
}

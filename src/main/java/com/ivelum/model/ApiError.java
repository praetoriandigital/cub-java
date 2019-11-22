package com.ivelum.model;

import java.util.HashMap;

public class ApiError extends CubObject {
  public Integer code;
  /**
   * General description for the errors.
   */
  public String description;
  /**
   * Contains specific errors per field.
   *
   * For example it could have the email key with "Not a valid email" value
   * for the updateEmail method.
   */
  public HashMap<String, String> params;
}

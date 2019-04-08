package com.ivelum.model;

import java.util.HashMap;

public class ApiError extends CubObject {
  public Integer code;
  public String description;
  public HashMap<String, String> params;
}

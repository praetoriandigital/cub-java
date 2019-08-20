package com.ivelum;

import com.ivelum.model.Factory;

public abstract class Cub {
  public static final String CHARSET = "UTF-8";
  public static final String VERSION = "0.10.0";
  public static volatile String apiKey;
  public static volatile String baseUrl = "https://id.lexipol.com/";
  public static final String version = "v1";

  public static final Factory factory = new Factory();
}
package com.ivelum.net;

public final class CubResponse {
  private String body;
  private int code;
  private String apiKey;

  public CubResponse(String body, int code, String apiKey) {
    this.body = body;
    this.code = code;
    this.apiKey = apiKey;
  }

  public String getBody() {
    return body;
  }

  public int getCode() {
    return code;
  }

  public String getApiKey() {
    return apiKey;
  }
}

package com.ivelum.net;

import com.ivelum.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Params {
  private final Map<String, Object> data = new HashMap<>();
  private String apiKey;

  public Params() {}

  public Params(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getApiKey() {
    return this.apiKey;
  }

  public Params setExpands(String... expands) {
    data.put("expand", StringUtils.join(",", Arrays.asList(expands)));
    return this;
  }

  public Params setOffset(long offset) {
    if (offset < 0) {
      throw new RuntimeException("Offset must me positive");
    }
    data.put("offset", offset);
    return this;
  }

  public boolean hasKey(String key) {
    if ("apiKey".equals(key)) {
      return true;
    }
    return data.containsKey(key);
  }

  public void setValue(String key, Object value) {
    data.put(key, value);
  }

  public Params setCount(int count) {
    if (count < 0) {
      throw new RuntimeException("Count must be positive");
    }
    data.put("count", count);
    return this;
  }

  public Map<String, Object> toMap() {
    return data;
  }
}

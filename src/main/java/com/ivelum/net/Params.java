package com.ivelum.net;

import com.ivelum.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Params {
  private final Map<String, Object> data = new HashMap<>();
  private String apiKey;

  /**
   * Default constructor. Default api key will be used.
   */
  public Params() {}

  /**
   * Sets apiKey for the next HTTP query with this params.
   *
   * @param apiKey apiKey to be used in the HTTP query with this params.
   */
  public Params(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getApiKey() {
    return this.apiKey;
  }

  /**
   * Allows to expand nested objects. More level of expands could be achivied using __. For example
   * state__organization.
   *
   * @param expands List of expands.
   * @return The same param object to use in chain calls.
   */
  public Params setExpands(String... expands) {
    data.put("expand", StringUtils.join(",", Arrays.asList(expands)));
    return this;
  }

  /**
   * Sets offset for this list query. Can be used for paginated response.
   *
   * @param offset Items number to skip. Can be used for paginated list response.
   * @return The same param object to use in chain calls.
   */
  public Params setOffset(long offset) {
    if (offset < 0) {
      throw new RuntimeException("Offset must me positive");
    }
    data.put("offset", offset);
    return this;
  }

  /**
   * Allows to check if params has requested key.
   *
   * @param key Key to check in Params object
   * @return result of check
   */
  public boolean hasKey(String key) {
    if ("apiKey".equals(key)) {
      return true;
    }
    return data.containsKey(key);
  }

  /**
   * Sets value to send in response.
   *
   * @param key name in the HTTP request. (GET or POST).
   * @param value to be sent in the HTTP request.
   * @return The same param object to use in chain calls.
   */
  public Params setValue(String key, Object value) {
    data.put(key, value);
    return this;
  }

  /**
   * Sets number of items in the response for the list query.
   *
   * @param count Number of items to be returned by list api call.
   * @return The same param object to use in chain calls.
   */
  public Params setCount(int count) {
    if (count < 0) {
      throw new RuntimeException("Count must be positive");
    }
    data.put("count", count);
    return this;
  }

  /**
   * Returns data for serialization.
   *
   * @return Map to be serialized in the HTTP request.
   */
  public Map<String, Object> toMap() {
    return data;
  }
}

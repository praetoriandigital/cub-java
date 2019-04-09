package com.ivelum.net;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import org.junit.Test;


public class ParamsTest {
  @Test
  public void testCreation() {
    String apiKey = "api";
    long offset = 10L;
    int count = 11;

    Params params = (new Params(apiKey)).setCount(count).setOffset(offset);

    assertEquals(apiKey, params.getApiKey());
    Map<String, Object> paramsAsMap = params.toMap();
    assertEquals(offset, paramsAsMap.get("offset"));
    assertEquals(count, paramsAsMap.get("count"));

    params = params.setValue("a", "a").setValue("b", "b");

    paramsAsMap = params.toMap();

    assertEquals("a", paramsAsMap.get("a"));
    assertEquals("b", paramsAsMap.get("b"));
  }
}
package com.ivelum;

import com.ivelum.Cub;

import java.io.InputStream;

public class CubModelBaseTest {
  static {
    Cub.apiKey = "pk_edc9e892474c450eb";
  }

  public String getFixture(String objectName) {
    InputStream is = this.getClass().getResourceAsStream(String.format("/%s.json", objectName));
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
}

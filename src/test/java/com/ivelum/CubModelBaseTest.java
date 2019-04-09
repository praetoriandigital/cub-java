package com.ivelum;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.InputStream;
import org.junit.Rule;


public class CubModelBaseTest {
  public final int wireMockPort = 8089;
  @Rule
  public final WireMockRule wireMockRule = new WireMockRule(wireMockPort);

  static {
    Cub.apiKey = "pk_edc9e892474c450eb";
  }

  public String getFixture(String objectName) {
    InputStream is = this.getClass().getResourceAsStream(String.format("/%s.json", objectName));
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  public void setMock(String objUrl, String fixtureName, int status, String apiKey) {
    stubFor(get(urlEqualTo(objUrl))
        .withHeader("Authorization", equalTo(String.format("Bearer %s", apiKey)))
            .willReturn(
                aResponse()
                .withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody(getFixture(fixtureName)
    )));
  }
}

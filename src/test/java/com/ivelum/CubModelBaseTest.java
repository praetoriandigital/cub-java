package com.ivelum;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.ivelum.net.ApiResource;
import java.io.InputStream;
import org.junit.After;
import org.junit.Rule;


public class CubModelBaseTest {
  @Rule
  public final WireMockRule wireMockRule = new WireMockRule(0);
  
  @After
  public void tearDown() {
    Cub.baseUrl = "https://id.lexipol.com/";
  }
  
  static {
    Cub.apiKey = "pk_edc9e892474c450eb";
  }

  public String getFixture(String objectName) {
    InputStream is = this.getClass().getResourceAsStream(String.format("/%s.json", objectName));
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  public void setGetMock(String objUrl, String fixtureName, int status, String apiKey) {
    Cub.baseUrl = String.format("http://127.0.0.1:%s/", wireMockRule.port());
    String endpoint = String.format("/%s%s", Cub.version, objUrl);
    stubFor(get(urlEqualTo(endpoint))
        .withHeader("Authorization", equalTo(String.format("Bearer %s", apiKey)))
            .willReturn(
                aResponse()
                .withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody(getFixture(fixtureName)
    )));
  }
  
  public void setPostMock(String objUrl, String fixtureName, int status, String apiKey) {
    Cub.baseUrl = String.format("http://127.0.0.1:%s/", wireMockRule.port());
    stubFor(post(urlEqualTo(objUrl))
        .withHeader("Authorization", equalTo(String.format("Bearer %s", apiKey)))
            .willReturn(
                aResponse()
                .withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody(getFixture(fixtureName)
    )));
  }
  
  protected void mockPostToListEndpoint(Class<?> cls, int status, String fixture, String apiKey) {
    String endpoint = String.format(
        "/%s%s",
        Cub.version,
        ApiResource.getListUrl(ApiResource.getClassUrl(cls)));
  
    setPostMock(endpoint, fixture, status, apiKey);
  }
  
  protected void mockPostToListEndpoint(String path, int status, String fixture, String apiKey) {
    Cub.baseUrl = String.format("http://127.0.0.1:%s/", wireMockRule.port());
    String endpoint = String.format("/%s%s", Cub.version, path);
    
    setPostMock(endpoint, fixture, status, apiKey);
  }
}

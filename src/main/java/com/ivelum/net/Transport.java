package com.ivelum.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.ivelum.Cub;
import com.ivelum.exception.ApiConnectionException;
import com.ivelum.exception.ApiException;
import com.ivelum.exception.CubException;
import com.ivelum.exception.InvalidRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Transport {

  public static CubResponse post(String endpoint, Params params)
          throws CubException, UnsupportedEncodingException {

    URL url = getUrlObj(absoluteUrl(endpoint));
    CubResponse resp;
    try {

      HttpURLConnection conn = getCubConnection(params, url);

      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", String.format(
              "application/x-www-form-urlencoded;charset=%s", Cub.CHARSET));

      try (OutputStream output = conn.getOutputStream()) {
        String query = Urlify.fromParams(params);
        output.write(query.getBytes(Cub.CHARSET));
      }

      int responseCode = conn.getResponseCode();

      InputStream st = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();

      String apiKey = getApiKeyOrDefault(params, null);

      resp = new CubResponse(readStream(st), responseCode, apiKey);
      if (resp.getCode() != 200) {
        handleResponseError(resp);
      }
    } catch (IOException e) {
      throw new ApiConnectionException("Api connection error", e);
    }

    return resp;
  }

  static CubResponse get(String endpoint, Params params)
          throws CubException, UnsupportedEncodingException {

    URL url = getUrlObj(absoluteUrl(endpoint, params));
    try {
      int responseCode;

      String apiKey = getApiKeyOrDefault(params, null);
      HttpURLConnection conn = getCubConnection(params, url);

      responseCode = conn.getResponseCode();
      InputStream st = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();
      CubResponse resp = new CubResponse(readStream(st), responseCode, apiKey);
      if (resp.getCode() != 200) {
        handleResponseError(resp);
      }
      return resp;
    } catch (IOException e) {
      throw new ApiConnectionException("Api connection error", e);
    }
  }

  private static HttpURLConnection getCubConnection(Params params, URL url) throws IOException {
    HttpURLConnection conn;
    conn = (HttpURLConnection) url.openConnection();
    for (Map.Entry<String, String> header : getHeaders(params).entrySet()) {
      conn.setRequestProperty(header.getKey(), header.getValue());
    }

    return conn;
  }

  private static URL getUrlObj(String absUrl) throws InvalidRequestException {
    try {
      return new URL(absUrl);
    } catch (MalformedURLException e) {
      throw new InvalidRequestException("Invalid cub url", e);
    }
  }

  private static String absoluteUrl(String endpoint)
          throws InvalidRequestException, UnsupportedEncodingException {

    return absoluteUrl(endpoint, null);
  }

  private static String absoluteUrl(String endpoint, Params params)
          throws InvalidRequestException, UnsupportedEncodingException {

    String absoluteEndpointUrl = String.format("%s%s%s", Cub.baseUrl, Cub.version, endpoint);
    if (params == null) {
      return absoluteEndpointUrl;
    }
    return formatUrl(absoluteEndpointUrl, Urlify.fromParams(params));
  }

  private static Map<String, String> getHeaders(Params params) {
    Map<String, String> headers = new HashMap<>();

    String javaVersion = System.getProperty("java.version");

    String userAgent = String.format(
            "Cub Client for Java, v%s %% version/%s", Cub.VERSION, javaVersion);
    headers.put("User-Agent", userAgent);

    headers.put("Accept-Charset", Cub.CHARSET);
    headers.put("Accept", "application/json");
    String apiKey = getApiKeyOrDefault(params, Cub.apiKey);

    headers.put("Authorization", String.format("Bearer %s", apiKey));

    String[] propertyNames = {"os.name", "os.version", "os.arch", "java.vendor", "java.vm.version",
      "java.vm.vendor"};
    Map<String, String> propertyMap = new HashMap<>();
    for (String propertyName : propertyNames) {
      propertyMap.put(propertyName, System.getProperty(propertyName));
    }
    propertyMap.put("bindings.version", Cub.VERSION);
    propertyMap.put("language", String.format("Java %s", javaVersion));
    propertyMap.put("publisher", "Ivelum");
    headers.put("X-Cub-User-Agent-Info", (new Gson()).toJson(propertyMap));

    return headers;
  }

  /**
   * Returns api key from params or default
   *
   * @return String
   */
  public static String getApiKeyOrDefault(Params params, String defaultKey) {
    if (params == null) {
      return defaultKey;
    }

    String paramsApiKey = params.getApiKey();
    if (paramsApiKey == null || paramsApiKey.length() < 1) {
      return defaultKey;
    }

    return paramsApiKey;
  }

  private static String formatUrl(String url, String query) {
    if (query == null || query.isEmpty()) {
      return url;
    } else {
      String separator = url.contains("?") ? "&" : "?";
      return String.format("%s%s%s", url, separator, query);
    }
  }

  private static String readStream(InputStream stream) {
    try (final Scanner scanner = new Scanner(stream, "UTF-8")) {
      // \A is the beginning of the stream boundary
      final String responseBody = scanner.useDelimiter("\\A").next();
      stream.close();
      return responseBody;
    } catch (IOException e) {
      // @todo: Parse error
      return "";
    }
  }

  private static void handleResponseError(CubResponse resp) throws CubException {
    try {
      JsonObject jsonObject = (new Gson()).fromJson(
              resp.getBody(), JsonObject.class);
      throw ApiException.fromJson(jsonObject);
    } catch (JsonSyntaxException e) {
      throw new InvalidRequestException(
              String.format("Api returned: %s, code=%d", resp.getBody(), resp.getCode()));
    }
  }
}

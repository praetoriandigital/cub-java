package com.ivelum.net;

import com.ivelum.exception.InvalidRequestException;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;


class Urlify {
  public static DateFormat getDateFormatter() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return dateFormat;
  }

  private static final class Parameter {
    final String key;
    final String value;

    Parameter(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }

  static String fromParams(Params params)
          throws InvalidRequestException, UnsupportedEncodingException {
    if (params == null) {
      return "";
    }

    Map<String, Object> paramsAsMap = params.toMap();

    StringBuilder queryStringBuffer = new StringBuilder();
    List<Parameter> flatParams = flattenParams(paramsAsMap);


    for (Parameter flatParam : flatParams) {
      if (queryStringBuffer.length() > 0) {
        queryStringBuffer.append("&");
      }
      queryStringBuffer.append(urlEncodePair(flatParam.key, flatParam.value));
    }

    return queryStringBuffer.toString();
  }

  private static List<Parameter> flattenParams(Map<String, Object> params)
          throws InvalidRequestException {
    return flattenParamsMap(params, null);
  }

  private static List<Parameter> flattenParamsMap(Map<String, Object> params, String keyPrefix)
          throws InvalidRequestException {
    List<Parameter> flatParams = new ArrayList<>();
    if (params == null) {
      return flatParams;
    }

    for (Map.Entry<String, Object> entry : params.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      String newPrefix = key;
      if (keyPrefix != null) {
        newPrefix = String.format("%s[%s]", keyPrefix, key);
      }

      flatParams.addAll(flattenParamsValue(value, newPrefix));
    }

    return flatParams;
  }

  private static String urlEncodePair(String k, String v)
          throws UnsupportedEncodingException {
    return String.format("%s=%s", ApiResource.urlEncode(k), ApiResource.urlEncode(v));
  }

  private static List<Parameter> flattenParamsValue(Object value, String keyPrefix)
          throws InvalidRequestException {
    List<Parameter> flatParams;

    if (value instanceof Map<?, ?>) {
      //noinspection unchecked
      flatParams = flattenParamsMap((Map<String, Object>) value, keyPrefix);
    } else if (value instanceof List<?>) {
      //noinspection unchecked
      flatParams = flattenParamsList((List<Object>) value, keyPrefix);
    } else if (value instanceof Object[]) {
      flatParams = flattenParamsArray((Object[]) value, keyPrefix);
    } else if (value instanceof Date) {
      flatParams = new ArrayList<>();
      flatParams.add(new Parameter(keyPrefix, getDateFormatter().format((Date) value)));
    } else if (value == null) {
      flatParams = new ArrayList<>();
      flatParams.add(new Parameter(keyPrefix, ""));
    } else {
      flatParams = new ArrayList<>();
      flatParams.add(new Parameter(keyPrefix, value.toString()));
    }

    return flatParams;
  }

  private static List<Parameter> flattenParamsList(List<Object> params, String keyPrefix)
          throws InvalidRequestException {
    List<Parameter> flatParams = new ArrayList<>();
    ListIterator<?> it = ((List<?>) params).listIterator();

    if (params.isEmpty()) {
      flatParams.add(new Parameter(keyPrefix, ""));
    } else {
      while (it.hasNext()) {
        String newPrefix = String.format("%s[%d]", keyPrefix, it.nextIndex());
        flatParams.addAll(flattenParamsValue(it.next(), newPrefix));
      }
    }

    return flatParams;
  }

  private static List<Parameter> flattenParamsArray(Object[] params, String keyPrefix)
          throws InvalidRequestException {
    List<Parameter> flatParams = new ArrayList<>();

    if (params.length == 0) {
      flatParams.add(new Parameter(keyPrefix, ""));
    } else {
      for (int i = 0; i < params.length; i++) {
        String newPrefix = String.format("%s[%d]", keyPrefix, i);
        flatParams.addAll(flattenParamsValue(params[i], newPrefix));
      }
    }

    return flatParams;
  }
}

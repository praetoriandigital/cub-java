package com.ivelum.net;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.ivelum.Cub;
import com.ivelum.exception.CubException;
import com.ivelum.model.CubObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class ApiResource extends CubObject {

  @SerializedName("object")
  public String objectName;

  public void reload() throws CubException {
    this.reload(null);
  }

  public void reload(Params params) throws CubException {
    String endPointUrl = getInstanceUrl(getInstanceName(this.getClass()), this.id);
    CubResponse resp = Transport.get(endPointUrl, params);
    Cub.factory.updateFromString(resp.getBody(), this, this.getInstanceCreator());
  }

  public void save() throws CubException {
    save(null);
  }

  public void save(Params params) throws CubException {

    String id = this.id;
    if (params == null) {
      params = new Params(this.apiKey);
    }

    if (id == null) {
      throw new RuntimeException("Save without id is not implemented");
    }

    String endPointUrl = getInstanceUrl(getInstanceName(this.getClass()), this.id);
    this.toParams(params);
    CubResponse resp = Transport.post(endPointUrl, params);
    Cub.factory.updateFromString(resp.getBody(), this, this.getInstanceCreator());

  }

  protected static List<CubObject> list(Class<?> cls) throws CubException {
    return list(cls, null);
  }

  protected static List<CubObject> list(Class<?> cls, Params params) throws CubException {
    String classUrl = getClassUrl(cls);
    List<CubObject> data = new LinkedList<>();
    String endPointUrl = getListUrl(classUrl);

    CubResponse resp = Transport.get(endPointUrl, params);
    JsonElement el = (new JsonParser()).parse(resp.getBody());
    for (JsonElement e : el.getAsJsonArray()) {
      data.add(Cub.factory.fromObject(e, resp.getApiKey()));
    }
    return data;
  }

  private static String getClassUrl(Class<?> cls) {
    String classUrl;
    try {
      // first search classUrl property
      classUrl = (String) cls.getDeclaredField("classUrl").get(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      // if nothing - build default
      classUrl = getListName(cls);
    }
    return classUrl;
  }

  private static String getListUrl(String instanceName) {
    return String.format("/%s/", instanceName);
  }

  public static String getInstanceUrl(String instanceName, String id) {
    return String.format("/%s/%s/", instanceName, id);
  }

  public static String getInstanceName(Class<?> cls) {
    try {
      return (String) cls.getDeclaredField("instanceName").get(null);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      return String.format("%ss", cls.getSimpleName().toLowerCase());
    }
  }

  private static String getListName(Class<?> cls) {
    return String.format("%ss", cls.getSimpleName().toLowerCase());
  }

  protected static CubObject get(String id, Class<?> cls, Params params) throws CubException {
    String endPointUrl = getInstanceUrl(getInstanceName(cls), id);
    CubResponse resp = Transport.get(endPointUrl, params);
    return Cub.factory.fromString(resp.getBody(), resp.getApiKey());
  }

  public static CubObject get(String id, Class<?> cls) throws CubException {
    return get(id, cls, null);
  }

  static String urlEncode(String str) throws UnsupportedEncodingException {
    if (str == null) {
      return null;
    } else {
      return URLEncoder.encode(str, Cub.CHARSET)
              .replaceAll("%5B", "[")
              .replaceAll("%5D", "]");
    }
  }
}
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

    String endPointUrl;
    if (id == null) {
      endPointUrl = getListUrl(getClassUrl(this.getClass()));
    } else {
      endPointUrl = getInstanceUrl(getInstanceName(this.getClass()), this.id);
    }
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

  public static String getClassUrl(Class<?> cls) {
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

  public static String getListUrl(String instanceName) {
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
    return ApiResource.getApi(endPointUrl, params);
  }

  public static CubObject get(String id, Class<?> cls) throws CubException {
    return get(id, cls, null);
  }
  
  /**
   * Deletes instance of class cls with id id.
   * @param id id of model to be deleted
   * @param cls model class
   * @param params params with api Key
   * @return true on success otherwise exception will be thrown
   * @throws CubException usually AccessDeniedException
   */
  public static boolean deleteById(String id, Class<?> cls, Params params) throws CubException {
    String endPointUrl = getInstanceUrl(getInstanceName(cls), id);
    return ApiResource.deleteApi(endPointUrl, params);
  }
  
  /**
   * Deletes instance of this model in lexipol.id
   *
   * @param params with api key
   * @throws CubException usually AccessDeniedException
   */
  public void delete(Params params) throws CubException {
    deleteById(this.id, this.getClass(), params);
  }
  
  /**
   * Deletes instance of this model in lexipol.id, will use api key same as for model receiving
   * @throws CubException usually AccessDeniedException
   */
  public void delete() throws CubException {
    Params params = new Params(this.getApiKey());
    delete(params);
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

  public static CubObject getApi(String url, Params params) throws CubException {
    CubResponse resp = Transport.get(url, params);
    return Cub.factory.fromString(resp.getBody(), resp.getApiKey());
  }

  public static CubObject postApi(String endpoint, Params params) throws CubException {
    CubResponse resp = Transport.post(endpoint, params);
    return Cub.factory.fromString(resp.getBody());
  }
  
  public static boolean deleteApi(String url, Params params) throws CubException {
    CubResponse resp = Transport.delete(url, params);
    return resp.getCode() == 200;
  }
}
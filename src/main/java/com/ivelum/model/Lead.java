package com.ivelum.model;

import com.google.gson.JsonObject;
import com.ivelum.net.Params;
import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;

import java.util.Date;
import java.util.List;


public class Lead extends ApiResource {
  public Date created;
  /**
   * Lead forms data is a json object that contains any data that depend on form.
   */
  public JsonObject data;
  public String email;
  public ExpandableField<Organization> organization;
  public Boolean production;
  public String remoteIp;
  public ExpandableField<Site> site;
  public String source;
  public String url;
  
  public static Lead get(String id) throws CubException {
    return (Lead) get(id, Lead.class, null);
  }
  
  public static Lead get(String id, Params params) throws CubException {
    return (Lead) get(id, Lead.class, params);
  }
  
  public static List<CubObject> list(Params params) throws CubException {
    return list(Lead.class, params);
  }
  
  public static List<CubObject> list(Date from, Params params) throws CubException {
    params.setValue("created__gt", from.getTime() / 1000);
    return list(params);
  }
  
  public static List<CubObject> list(Date from, Date to, Params params) throws CubException {
    params.setValue("created__lt", to.getTime() / 1000);
    return list(from, params);
  }
}

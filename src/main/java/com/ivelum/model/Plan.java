package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

public class Plan extends ApiResource {
  public Integer amount;
  public String description;
  public String interval;
  public Integer intervalCount;
  public ExpandableField<Product> product;
  public Boolean requireVerification;
  
  public static Plan get(String id) throws CubException {
    return (Plan) get(id, Plan.class, null);
  }
  
  public static Plan get(String id, Params params) throws CubException {
    return (Plan) get(id, Plan.class, params);
  }
}

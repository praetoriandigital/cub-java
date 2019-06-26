package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

public class Sku extends ApiResource {
  public Boolean isActive;
  public Integer price;
  public ExpandableField<Product> product;
  
  public static Sku get(String id) throws CubException {
    return (Sku) get(id, Sku.class, null);
  }
  
  public static Sku get(String id, Params params) throws CubException {
    return (Sku) get(id, Sku.class, params);
  }
}

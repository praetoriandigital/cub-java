package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.util.List;

public class Product extends ApiResource {
  
  public String name;
  public Boolean isActive;
  public String type;
  public List<ExpandableField<Sku>> skus;
  
  public static Product get(String id) throws CubException {
    return (Product) get(id, Product.class, null);
  }
  
  public static Product get(String id, Params params) throws CubException {
    return (Product) get(id, Product.class, params);
  }
}

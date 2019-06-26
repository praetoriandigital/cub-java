package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;
import java.util.List;


public class Customer extends ApiResource {
  public ExpandableField<Organization> organization;
  public ExpandableField<User> user;
  public List<ExpandableField<ServiceSubscription>> serviceSubscriptions;
  
  
  public static Customer get(String id) throws CubException {
    return (Customer) get(id, Customer.class, null);
  }
  
  public static Customer get(String id, Params params) throws CubException {
    return (Customer) get(id, Customer.class, params);
  }
}

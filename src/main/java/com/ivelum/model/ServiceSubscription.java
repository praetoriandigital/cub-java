package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.util.Date;
import java.util.List;

public class ServiceSubscription extends ApiResource {
  public Date activeSince;
  public Date activeTill;
  public Boolean cancelAtPeriodEnd;
  public String coupon;
  public ExpandableField<Customer> customer;
  public Float discountAmountOff;
  public Date discountEnd;
  public Float discountPercentOff;
  public Date discountStart;
  public ExpandableField<Plan> plan;
  public Boolean verificationPending;
  
  
  public static List<CubObject> listByOrg(String orgId, Params params) throws CubException {
    params.setValue("customer__organization", orgId);
    return list(params);
  }
  
  public static List<CubObject> list(Params params) throws CubException {
    return list(ServiceSubscription.class, params);
  }
  
  public static ServiceSubscription get(String id) throws CubException {
    return (ServiceSubscription) get(id, ServiceSubscription.class, null);
  }
  
  public static ServiceSubscription get(String id, Params params) throws CubException {
    return (ServiceSubscription) get(id, ServiceSubscription.class, params);
  }
}

package com.ivelum.model;

import com.google.gson.JsonObject;
import com.ivelum.exception.CubException;
import com.ivelum.exception.InvalidRequestException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.util.Date;
import java.util.List;

public class Organization extends ApiResource {
  public String address;
  public String city;
  public String county;
  public String email;
  public String employees;
  public String fax;
  public String hrPhone;
  public String logo;
  public Boolean moderatorApproved;
  public String name;
  public String phone;
  public String postalCode;
  public String website;
  public JsonObject metadata;
  public Date created;
  public Date modified;
  public List<String> tags;
  public ExpandableField<State> state;
  public ExpandableField<Country> country;

  public static Organization get(String id) throws CubException {
    return (Organization) get(id, Organization.class, null);
  }

  public static Organization get(String id, Params params) throws CubException {
    return (Organization) get(id, Organization.class, params);
  }

  public static List<CubObject> list(Params params) throws CubException {
    return list(Organization.class, params);
  }

  public static List<CubObject> list() throws CubException {
    return list(Organization.class);
  }
  
  @Override
  public void toParams(Params params) throws InvalidRequestException {
    super.toParams(params);
    if (this.tags != null && this.tags.size() > 0) {
      params.setValue("tags", String.join(",", this.tags));
    }
  }
}

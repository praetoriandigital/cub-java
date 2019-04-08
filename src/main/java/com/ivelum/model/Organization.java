package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.io.UnsupportedEncodingException;
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
  public Date created;
  public Date modified;
  public List<String> tags;
  public ExpandableField<State> state;
  public ExpandableField<Country> country;

  public static Organization get(String id) throws CubException, UnsupportedEncodingException {
    return (Organization) get(id, Organization.class, null);
  }

  public static Organization get(String id, Params params)
          throws CubException, UnsupportedEncodingException {
    return (Organization) get(id, Organization.class, params);
  }

  public static List<CubObject> list(Params params)
          throws CubException, UnsupportedEncodingException {
    return list(Organization.class, params);
  }

  public static List<CubObject> list() throws CubException, UnsupportedEncodingException {
    return list(Organization.class);
  }
}
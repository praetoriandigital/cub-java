package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class Country extends ApiResource {
  public static final String classUrl = "countries";
  public static final String instanceName = "country";
  public Integer code;
  public String name;
  public String code2;
  public String code3;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Country)) {
      return false;
    }
    Country another = (Country) o;
    if (!id.equals(another.id)) {
      return false;
    }

    if (!name.equals(another.name)) {
      return false;
    }

    if (!code2.equals(another.code2)) {
      return false;
    }

    return code3.equals(another.code3);
  }

  public static List<CubObject> list() throws CubException, UnsupportedEncodingException {
    return list(Country.class);
  }

  public static List<CubObject> list(Params params)
          throws CubException, UnsupportedEncodingException {
    return list(Country.class, params);
  }

  public static Country get(String id) throws CubException, UnsupportedEncodingException {
    return (Country) get(id, Country.class, null);
  }
}
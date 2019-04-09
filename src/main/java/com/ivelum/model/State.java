package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class State extends ApiResource {
  public String code;
  public String name;
  public ExpandableField<Country> country;


  public static List<CubObject> list(Params params)
          throws CubException, UnsupportedEncodingException {

    return list(State.class, params);
  }
}

package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;

import java.util.List;

public class Site extends ApiResource {
  public String domain;
  public List<String> tags;

  public static Site get(String id) throws CubException {
    return (Site) get(id, Site.class);
  }
}

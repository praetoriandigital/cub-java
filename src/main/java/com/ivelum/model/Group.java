package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.util.Date;
import java.util.List;

public class Group extends ApiResource {

  static final class ActiveMembers {
    public Integer invited;
    public Integer joined;
  }

  public ActiveMembers activeMembers;
  public Date created;
  public String description;
  public String name;
  public ExpandableField<Organization> organization;
  public String type;

  public static Group get(String id, Params params) throws CubException {
    return (Group) get(id, Group.class, params);
  }

  public static List<CubObject> list(Params params) throws CubException {
    return list(Group.class, params);
  }
}

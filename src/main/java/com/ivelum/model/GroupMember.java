package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.util.Date;
import java.util.List;

public class GroupMember extends ApiResource {
  public ExpandableField<Group> group;
  public ExpandableField<Member> member;
  public Boolean isAdmin;
  public Date created;

  public static GroupMember get(String id, Params params) throws CubException {

    return (GroupMember) get(id, GroupMember.class, params);
  }

  public static List<CubObject> list(Params params) throws CubException {

    return list(GroupMember.class, params);
  }
}

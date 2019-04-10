package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

public class Member extends ApiResource {
  public Date created;
  public Boolean isActive;
  public Boolean isAdmin;
  public Boolean isProfileEditable;
  public String notes;
  public ExpandableField<Organization> organization;
  public String personalId;
  public ExpandableField<User> user;
  public List<ExpandableField<MemberPosition>> positions;
  public List<ExpandableField<GroupMember>> groupMembership;

  public static Member get(String id) throws CubException, UnsupportedEncodingException {
    return (Member) get(id, Member.class);
  }

  public static Member get(String id, Params params)
          throws CubException, UnsupportedEncodingException {

    return (Member) get(id, Member.class, params);
  }
}

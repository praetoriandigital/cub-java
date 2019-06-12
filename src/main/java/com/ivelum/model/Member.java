package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

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

  public static Member get(String id) throws CubException {

    return (Member) get(id, Member.class);
  }

  public static Member get(String id, Params params) throws CubException {

    return (Member) get(id, Member.class, params);
  }
  
  /**
   * Invites exists lexipol id user to the organization by email
   * @param orgId organization id
   * @param email lexipol user email
   * @param params params with api key that has permissions invite into organization
   * @return Invited Member instance
   * @throws CubException BadRequestException for data validation, AccessDeniedException
   */
  public static Member invite(String orgId, String email, Params params) throws CubException {
    Member member = new Member();
    member.organization = new ExpandableField<>(orgId);
  
    params.setValue("email", email);
    
    member.save(params);
    return member;
  }
}

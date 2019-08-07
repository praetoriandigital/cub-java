package com.ivelum.model;

import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;

import java.util.Date;
import java.util.List;

public class Member extends ApiResource {
  public Date created; // Server side field, can not be changed or updated with API.
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

  /**
   * Sets member permissions
   * @param id id of member object
   * @param isAdmin new isAdmin status, you can use null to skip update for this field
   * @param isActive new isActive status, you can user null to skip update for this field
   * @param params params object with the api key.
   * @return Updated member object
   * @throws CubException Usually ApiError
   */
  public static Member setPermissions(
      String id, Boolean isAdmin, Boolean isActive, Params params) throws CubException {
    return setPermissions(id, isAdmin, isActive, null, null, params);
  }
  
  /**
   * Sets member permissions.
   *
   * siteId ad senderId will be used to send email about active status changes.
   * This behaviour of the site can be changed in the LID ADMIN
   *
   * @param id id of member object
   * @param isAdmin new isAdmin status, you can use null to skip update for this field
   * @param isActive new isActive status, you can user null to skip update for this field
   * @param senderId id of user who did update, will be used in LID email system
   * @param siteId id of site where update was done, will be used in LID email system
   * @param params params object with the api key.
   * @return Updated member object
   * @throws CubException Usually ApiError
   */
  public static Member setPermissions(
      String id, Boolean isAdmin, Boolean isActive, String senderId, String siteId, Params params)
      throws CubException {
    if (isAdmin != null) {
      params.setValue("is_admin", isAdmin);
    }
    if (isActive != null) {
      params.setValue("is_active", isActive);
    }
    if (senderId != null && siteId != null) {
      params.setValue("notification_site", siteId);
      params.setValue("senderId", senderId);
    }
    String endpoint = String.format("/%s/%s/permissions", getInstanceName(Member.class), id);
    return (Member) ApiResource.postApi(endpoint, params);
  }
  
}

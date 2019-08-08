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
   * Invites user to the organization by email
   * @param orgId organization id
   * @param params params with api key that has permissions invite into organization
   * @return Invited Member instance
   * @throws CubException BadRequestException for data validation, AccessDeniedException
   */
  public static Member invite(String orgId, Params params) throws CubException {
    Member member = new Member();
    member.organization = new ExpandableField<>(orgId);
    member.save(params);
    return member;
  }

  /**
   * Invites existent lexipol id user to the organization by email
   * @param orgId organization id
   * @param email lexipol user email
   * @param params params with api key that has permissions invite into organization
   * @return Invited Member instance
   * @throws CubException BadRequestException for data validation, AccessDeniedException
   */
  public static Member invite(String orgId, String email, Params params) throws CubException {
    params.setValue("email", email);
    return invite(orgId, params);
  }

  /**
   * Invites new user to the organization by email
   * @param orgId organization id
   * @param email user email
   * @param firstName user first name
   * @param lastName user last name
   * @param personalId user personal id (badge, not lexipol UID)
   * @param senderId id of user who did update, will be used in LID email system
   * @param siteId id of site where update was done, will be used in LID email system
   * @param params params with api key that has permissions invite into organization
   * @return Invited Member instance
   * @throws CubException BadRequestException for data validation, AccessDeniedException
   */
  public static Member inviteNewUser(
          String orgId, String email, String firstName, String lastName,
          String personalId, String senderId, String siteId, Params params) throws CubException {
    if (firstName != null) {
      params.setValue("first_name", firstName);
    }
    if (lastName != null) {
      params.setValue("last_name", lastName);
    }
    if (personalId != null) {
      params.setValue("personal_id", personalId);
    }
    params.setValue("create_user", true);
    params.setValue("notification_site", siteId);
    params.setValue("notification_sender", senderId);
    return invite(orgId, email, params);
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
      params.setValue("notification_sender", senderId);
    }
    String endpoint = String.format("/%s/%s/permissions", getInstanceName(Member.class), id);
    return (Member) ApiResource.postApi(endpoint, params);
  }
  
}

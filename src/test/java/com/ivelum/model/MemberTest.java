package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.AccessDeniedException;
import com.ivelum.exception.BadRequestException;
import com.ivelum.exception.CubException;
import com.ivelum.exception.DeserializationException;

import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;
import java.util.List;
import org.junit.Test;


public class MemberTest extends CubModelBaseTest {
  @Test
  public void testDeserialization() throws DeserializationException {
    String memberResponse = getFixture("member");

    Member member = (Member) Cub.factory.fromString(memberResponse);

    assertFalse(member.isActive);
    assertFalse(member.isAdmin);
    assertFalse(member.isProfileEditable);
    assertEquals("notes", member.notes);
    assertEquals("personalId", member.personalId);
    assertFalse(member.user.isExpanded());
    assertEquals("usr_123", member.user.getId());
    assertNull(member.deleted);
    assertFalse(member.organization.isExpanded());
    assertEquals("org_123", member.organization.getId());

    List<ExpandableField<MemberPosition>> positions = member.positions;

    assertEquals(1, positions.size());
    assertFalse(positions.get(0).isExpanded());
    assertEquals("mpo_123", positions.get(0).getId());

    List<ExpandableField<GroupMember>> groupMembership = member.groupMembership;

    assertEquals(1, groupMembership.size());
    assertFalse(groupMembership.get(0).isExpanded());
    assertEquals("grm_123", groupMembership.get(0).getId());
  }

  @Test
  public void testDeserializationDeletedWebhook() throws DeserializationException {
    String memberResponse = getFixture("member_deleted");

    Member member = (Member) Cub.factory.fromString(memberResponse);
    assertTrue(member.deleted);
  }
  
  @Test
  public void testAddMemberSuccess() throws CubException {
    Member memberFromFixture = (Member) Cub.factory.fromString(getFixture("member"));
    String adminToken = "tokenwithadminrights";
    mockPostToListEndpoint(Member.class, 200, "member", adminToken);
  
    Member member = Member.invite(
        memberFromFixture.organization.getId(), "valid@lexipolid.email", new Params(adminToken));
    
    assertEquals(member.id, memberFromFixture.id);
    assertEquals(member.user.getId(), memberFromFixture.user.getId());
  }

  @Test
  public void testAddMemberAndCreateUser() throws CubException {
    Member memberFromFixture = (Member) Cub.factory.fromString(getFixture("member"));
    String appKey = "secret_key";
    mockPostToListEndpoint(Member.class, 200, "member", appKey);

    String objUrl = ApiResource.getInstanceUrl(ApiResource.getInstanceName(User.class), "usr_123");
    setGetMock(objUrl, "user", 200, appKey);

    String orgId = memberFromFixture.organization.getId();
    String inviterId = "usr_567";
    String siteId = "ste_123";
    String email = "testuser@ivelum.com";

    Member member = Member.inviteNewUser(
            orgId, email, "firstName", "lastName", null,
            inviterId, siteId, new Params(appKey));

    User user = User.get(member.user.getId(), new Params(appKey));
    assertEquals(member.id, memberFromFixture.id);
    assertEquals(user.id, memberFromFixture.user.getId());
  }

  @Test
  public void testAddMemberWithInvalidEmail() throws CubException {
    String token = "123";
    mockPostToListEndpoint(Member.class, 400, "validation_error_email_not_exists", token);
    try {
      Member.invite("org_123", "non@lexipolid.email", new Params(token));
    } catch (BadRequestException e) {
      assertTrue(e.getApiError().params.get("email").contains("no account"));
    }
  }
  
  @Test
  public void testAlreadyExistsMember() throws CubException {
    String token = "123";
    mockPostToListEndpoint(Member.class, 400, "validation_error_user_already_member", token);
    try {
      Member.invite("org_123", "already@member.email", new Params(token));
    } catch (BadRequestException e) {
      assertTrue(e.getApiError().description.contains("already a member"));
    }
  }
  
  @Test
  public void testNoPermissions() throws CubException {
    
    String token = "123";
    mockPostToListEndpoint(
        Member.class, 403, "validation_error_not_allowed_to_invite_member", token);
    try {
      Member.invite("org_123", "valid@lixipolid.email", new Params(token));
    } catch (AccessDeniedException e) {
      assertTrue(e.getApiError().description.contains("is not allowed to invite new members to"));
    }
  }
  
  @Test
  public void testSetActive() throws CubException {
    String memberId = "mbr_001";
    String apiKey = "apiKey";
    String endpoint = String.format(
        "/%s/%s/%s/permissions", Cub.version, ApiResource.getInstanceName(Member.class), memberId);
    
    setPostMock(endpoint, "member_active_and_admin", 200, apiKey);
    Member member = Member.setPermissions(memberId, true, true, new Params(apiKey));
    
    assertTrue(member.isAdmin);
    assertTrue(member.isActive);
  }
}

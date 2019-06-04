package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.*;
import com.ivelum.net.Params;
import java.util.List;

import org.junit.Test;



public class UserTest extends CubModelBaseTest {
  static final String test_username = "support@ivelum.com";
  static final String test_userpassword = "SJW8Gg";

  @Test
  public void testLogin() throws CubException {
    User user = User.login(test_username, test_userpassword);
    assertNotNull(user.dateJoined);
    assertEquals(user.email, test_username);
    assertNotNull(user.token);
    assertNotNull(user.lastLogin);
    assertEquals("", user.middleName);
    assertEquals(user.token, user.getApiKey());

    User userCopy = User.get(user.id, new Params(user.token));
    assertEquals(user.token, userCopy.getApiKey());

    assertEquals(userCopy.id, user.id);
    assertEquals(userCopy.username, user.username);
    assertEquals(userCopy.email, user.email);
  }

  @Test
  public void testGetUserAndReissueToken() throws CubException {
    // invalid user token
    try {
      User.getUserAndReissueToken("invalid token");
    } catch (ApiException e) {
      assertEquals("You did not provide a valid API key.", e.getMessage());
    }

    User user = User.login(test_username, test_userpassword);
    String token = user.getApiKey();
    // invalid application key
    try {
      User.getUserAndReissueToken(token, "invalid_app_key");
    } catch (BadRequestException e) {
      assertTrue(e.getApiError().description.contains("app_key"));
    }

    // success
    User userCopy = User.getUserAndReissueToken(token);

    assertEquals(userCopy.id, user.id);
    assertEquals(userCopy.email, user.email);

  }

  @Test
  public void testDeserialization() throws DeserializationException {
    String jsonStr = getFixture("user");
    User user = (User) Cub.factory.fromString(jsonStr);

    assertEquals("firstName", user.firstName);
    assertEquals("lastName", user.lastName);
    assertEquals("middleName", user.middleName);
    assertEquals("token", user.token);
    assertEquals("photo_large.png", user.photoLarge);
    assertEquals("photo_small.png", user.photoSmall);
    assertEquals("username", user.username);
    assertEquals((Integer) 0, user.invitationSentCount);

    assertNotNull(user.birthDate);
    assertFalse(user.emailConfirmed);
    assertFalse(user.emailSetRequired);
    assertEquals("male", user.gender);
    assertFalse(user.invalidEmail);
    assertNotNull(user.invitationLastSentOn);
    assertEquals("user", user.objectName);
    assertEquals("originalUsername", user.originalUsername);
    assertFalse(user.passwordChangeRequired);
    assertFalse(user.retired);
    assertFalse(user.purchasingRoleBuyForOrganization);
    assertFalse(user.purchasingRoleBuyForSelfOnly);
    assertFalse(user.purchasingRoleRecommend);
    assertFalse(user.purchasingRoleSpecifyForOrganization);
    assertEquals(1, user.verifiedTags.size());
    assertTrue(user.verifiedTags.contains("Law Enforcement"));

    List<ExpandableField<Member>> members = user.membership;
    assertEquals(1, members.size());
    ExpandableField<Member> member = members.get(0);
    assertFalse(member.isExpanded());
    assertEquals("mbr_123", member.getId());

    assertFalse(user.registrationSite.isExpanded());
    assertEquals("ste_123", user.registrationSite.getId());

    assertEquals(user.token, user.getApiKey());
    assertNull(user.deleted);
  }

  @Test
  public void testDeserializationDeletedObject() throws DeserializationException {
    String jsonStr = getFixture("user_deleted");
    User user = (User) Cub.factory.fromString(jsonStr);
    assertTrue(user.deleted);
  }

  @Test
  public void testTokenNotSerialized() throws InvalidRequestException {
    User user = new User();
    Params params = new Params();
    user.toParams(params);
    assertTrue(params.hasKey("email"));
    assertFalse(params.hasKey("token"));
    assertTrue(User.serializationIgnoreFields.contains("token"));
  }

  @Test
  public void testRegister() throws CubException {
    try {
      User.register("", "", "", "", "");
      fail("BadRequestException is expected");
    } catch (BadRequestException e) {
      ApiError apiError = e.getApiError();
      assert apiError.params.get("password").contains("required");
      assert apiError.params.get("registration_site").contains("required");
      assert apiError.params.get("last_name").contains("required");
      assert apiError.params.get("first_name").contains("required");
      assert apiError.params.get("email").contains("required");
    }

    try {
      User.register("fname", "laname", test_username, "any", "any");
      fail("BadRequestException is expected");
    } catch (BadRequestException e) {
      ApiError apiError = e.getApiError();
      assert apiError.params.get("password").contains("length must be");
      assert apiError.params.get("registration_site").contains("does not exist");
      assert apiError.params.get("email").contains("already used");
    }
  }
}
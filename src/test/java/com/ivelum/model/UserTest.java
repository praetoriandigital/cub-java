package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.exception.DeserializationException;
import com.ivelum.exception.InvalidRequestException;
import com.ivelum.net.Params;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;



public class UserTest extends CubModelBaseTest {
  static final String test_username = "support@ivelum.com";
  static final String test_userpassword = "SJW8Gg";

  @Test
  public void testLogin() throws CubException, UnsupportedEncodingException {
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
}
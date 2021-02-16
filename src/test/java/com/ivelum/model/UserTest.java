package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.ApiException;
import com.ivelum.exception.BadRequestException;
import com.ivelum.exception.CubException;
import com.ivelum.exception.DeserializationException;
import com.ivelum.exception.InvalidRequestException;
import com.ivelum.exception.LookupAccountNotFoundException;
import com.ivelum.exception.UnauthorizedException;
import com.ivelum.exceptions.LoginErrorExampleException;
import com.ivelum.exceptions.PasswordChangeRequiredExampleException;
import com.ivelum.net.Params;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class UserTest extends CubModelBaseTest {
  private static final String testUsername = "support@ivelum.com";
  private String testUserPassword = "";

  @Before
  public void initTestUserPass() {
    Map<String, String> env = System.getenv();
    this.testUserPassword = env.get("TEST_USER_PASS");
  }

  @Test
  public void testLogin() throws CubException {
    User user = User.login(testUsername, testUserPassword);
    assertNotNull(user.dateJoined);
    assertEquals(user.email, testUsername);
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
  public void testLoginFailed() throws CubException {
    try {
      User.login(testUsername, "invalid_password");
    } catch (BadRequestException e) {
      assertTrue(e.getApiError().description.contains("correct username and password"));
    }
  }
  
  /**
   * Shortcat for the login without site
   *
   * @param username email or username
   * @param password user password
   * @return user that logged in
   */
  private User loginWithExtraErrorHandlingExample(String username, String password)
      throws CubException {
    return loginWithExtraErrorHandlingExample(username, password, null);
  }
  
  /**
   * Example of handling login errors.
   *
   * Usually BadRequestException.getApiError().description contains user-friendly message
   * with html mark up and this information provides usefull information for the user.
   * BadRequestException.getApiError().params - contains the fields specific error description.
   * And special keys is BadRequestException.getApiError().params.get("redirect") that contains
   * redirectUrl for the user to update password. The redirectUrl will be in error
   * only when site was passed to the login method.
   * This example provides two extra exceptions.
   *
   * @param username email or username for login
   * @param password user password
   * @param site the site uid where user is trying to log in. Site must be connected to the app used
   * @return user that logged in
   */
  private User loginWithExtraErrorHandlingExample(String username, String password, String site)
      throws CubException {
    try {
      return User.login(username, password, site);
    } catch (BadRequestException e) {
      ApiError apiError = e.getApiError();
      // special login error with link to change password
      if (apiError.params != null && apiError.params.containsKey("redirect")) {
        throw new PasswordChangeRequiredExampleException(apiError);
      }
      // general login error that have description with details
      throw new LoginErrorExampleException(apiError);
    }
  }
  
  /**
   * Example of update password required situation.
   *
   * Checking user that must update password. And we didn't send site to the login method.
   * See response example in the fixture
   */
  @Test
  public void testLoginPasswordChangeRequiredWithoutSite() throws CubException {
    String endpoint = String.format("/%s/login", User.classUrl);
    mockPostToListEndpoint(endpoint, 400, "login_change_password_required_wo_site", Cub.apiKey);
    boolean raised = false;
    try {
      loginWithExtraErrorHandlingExample("any@localhost", "anypass");
    } catch (LoginErrorExampleException e) {
      // LoginError has error message from response.
      String errDesc = e.getApiError().description;
      assertTrue(errDesc.contains("You need to change your password before proceeding"));
      raised = true;
    }
    assertTrue(raised);
  }
  
  
  /**
   * Example of update password required situation.
   *
   * Checking user that must update password. And we sent site to the login method.
   * See response example in the fixture
   */
  @Test
  public void testLoginPasswordChangeRequiredWithSite() throws CubException {
    String siteUid = "ste_any";
    String endpoint = String.format("/%s/login", User.classUrl);
    mockPostToListEndpoint(endpoint, 400, "login_change_password_required_with_site", Cub.apiKey);
  
    boolean changeEmailRequired = false;
    try {
      loginWithExtraErrorHandlingExample("any@localhost", "anypass", siteUid);
    } catch (PasswordChangeRequiredExampleException e) {
      changeEmailRequired = true;
      assertTrue(e.getRedirectUrl().contains("http://localhost")); // Redirect user to the e.getRedirectUrl();
    }
    assertTrue(changeEmailRequired);
  }
  
  /**
   * Example of update password required situation.
   *
   * Checking user that must update password by email. And we don't send site to the login method.
   * See response example in the fixture
   */
  @Test
  public void testLoginPasswordChangeByEmailRequiredWithoutSite() throws CubException {
    String endpoint = String.format("/%s/login", User.classUrl);
    mockPostToListEndpoint(
        endpoint, 400, "login_password_change_by_email_required_wo_site", Cub.apiKey);
    boolean raised = false;
    try {
      loginWithExtraErrorHandlingExample("any@localhost", "anypass");
    } catch (LoginErrorExampleException e) {
      // LoginError has error message from response.
      assertTrue(e.getApiError().description.contains("The password you entered was valid, but"));
      raised = true;
    }
    assertTrue(raised);
  }
  
  @Test
  public void testLoginForInactiveUser() throws CubException {
    String endpoint = String.format("/%s/login", User.classUrl);
    mockPostToListEndpoint(
        endpoint, 400, "user_login_on_site_access_denied", Cub.apiKey);
    boolean raised = false;
    try {
      loginWithExtraErrorHandlingExample("any@localhost", "anypass", "ste_any");
    } catch (LoginErrorExampleException e) {
      // LoginError has error message from response.
      assertTrue(e.getApiError().description.contains("Access denied. Your account"));
      raised = true;
    }
    assertTrue(raised);
  }
  
  /**
   * Example of update password required situation.
   *
   * Checking user that must update password by email. And we sent site to the login method.
   * See response example in the fixture
   */
  @Test
  public void testLoginPasswordChangeByEmailRequiredWithSite() throws CubException {
    String siteUid = "ste_any";
    String endpoint = String.format("/%s/login", User.classUrl);
    mockPostToListEndpoint(
        endpoint, 400, "login_password_change_by_email_required_with_site", Cub.apiKey);
    boolean raised = false;
    try {
      loginWithExtraErrorHandlingExample("any@localhost", "any", siteUid);
    } catch (LoginErrorExampleException e) {
      raised = true;
      assertTrue(
          e.getApiError().description.contains("Please check your email account for an email"));
    }
    assertTrue(raised);
  }

  @Test
  public void testGetUserAndReissueToken() throws CubException {
    // invalid user token
    try {
      User.getUserAndReissueToken("invalid token");
    } catch (ApiException e) {
      assertEquals("You did not provide a valid API key.", e.getMessage());
    }

    User user = User.login(testUsername, testUserPassword);
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
  public void testUpdateUserName() throws CubException {
    User user = User.login(testUsername, testUserPassword);

    boolean failed = false;
    try {
      User.updateUsername("new_user_name", "invalid_password", new Params(user.getApiKey()));
    } catch (BadRequestException e) {
      assertTrue(e.getApiError().description.contains("password"));
      failed = true;
    }

    assertTrue(failed);

    failed = false;
    // invalid token
    try {
      User.updateUsername("new_user_name", "invalid_password", new Params("invalid token"));
    } catch (ApiException e) {
      assertEquals("You did not provide a valid API key.", e.getMessage());
      failed = true;
    }

    assertTrue(failed);
  }

  @Test
  public void testUpdateEmail() throws CubException {
    User user = User.login(testUsername, testUserPassword);

    boolean failed = false;
    try {
      User.updateEmail(
              "some@mail.com",
              "invalid_password",
              "site_id",
              new Params(user.getApiKey()));
    } catch (BadRequestException e) {
      assertTrue(e.getApiError().description.contains("password"));
      failed = true;
    }

    assertTrue(failed);

    failed = false;
    // invalid token
    try {
      User.updateEmail(
              "new_user_email",
              "invalid_password",
              "site_id",
              new Params("invalid token"));
    } catch (ApiException e) {
      assertEquals("You did not provide a valid API key.", e.getMessage());
      failed = true;
    }

    assertTrue(failed);
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
      User.register("", "", "", "", "", new Params());
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
      User.register("fname", "laname", testUsername, "any", "any", new Params());
      fail("BadRequestException is expected");
    } catch (BadRequestException e) {
      ApiError apiError = e.getApiError();
      assert apiError.params.get("password").contains("too short");
      assert apiError.params.get("registration_site").contains("does not exist");
      assert apiError.params.get("email").contains("already used");
    }
  }
  
  @Test
  public void testRegisterSuccess() throws CubException {
    User fixtureUser = (User) Cub.factory.fromString(getFixture("user"));
    String apiKey = "apiKey";
    String endpoint = String.format("/%s/register", User.classUrl);
    mockPostToListEndpoint(endpoint, 200, "user", apiKey);
    // register user
    User user = User.register(
        fixtureUser.firstName,
        fixtureUser.lastName,
        fixtureUser.email,
        "123123123",
        fixtureUser.registrationSite.getId(),
        new Params(apiKey));
    // check just created users
    assertEquals("token", user.getApiKey());
    assertEquals(user.id, fixtureUser.id);
    assertEquals(user.firstName, fixtureUser.firstName);
  }
  
  @Test
  public void testRegisterWithoutPasswordRequiredParamss() throws CubException {
    try {
      User.registerWithoutPassword("", "", "", "", new Params());
      fail("BadRequestException is expected");
    } catch (BadRequestException e) {
      ApiError apiError = e.getApiError();
      assert apiError.params.get("registration_site").contains("required");
      assert apiError.params.get("email").contains("required");
    }
    
    try {
      User.registerWithoutPassword("fname", "laname", testUsername, "any", new Params());
      fail("BadRequestException is expected");
    } catch (BadRequestException e) {
      ApiError apiError = e.getApiError();
      assert apiError.params.get("registration_site").contains("does not exist");
      assert apiError.params.get("email").contains("already used");
    }
  }
  
  @Test
  public void testRegisterWithoutPasswordSuccess() throws CubException {
    User fixtureUser = (User) Cub.factory.fromString(getFixture("user"));
    String apiKey = "apiKey";
    String endpoint = String.format("/%s/register-without-password", User.classUrl);
    mockPostToListEndpoint(endpoint, 200, "user", apiKey);
    // register user
    Params params = new Params(apiKey);
    params.setValue("middle_name", fixtureUser.middleName);
    User user = User.registerWithoutPassword(
        fixtureUser.firstName,
        fixtureUser.lastName,
        fixtureUser.email,
        fixtureUser.registrationSite.getId(),
        params);
    // check just created users
    assertEquals(user.id, fixtureUser.id);
    assertEquals(user.firstName, fixtureUser.firstName);
  }

  @Test
  public void testCreateWithPasswordHash() throws CubException {
    User fixtureUser = (User) Cub.factory.fromString(getFixture("user"));
    String apiKey = "apiKey";
    mockPostToListEndpoint("/users/create-with-password-hash", 200, "user", apiKey);
    // register user
    Params params = new Params(apiKey);
    params.setValue("middle_name", fixtureUser.middleName);
    User user = User.createWithPasswordHash(
        fixtureUser.firstName,
        fixtureUser.lastName,
        fixtureUser.email,
        fixtureUser.registrationSite.getId(),
        "test_hash",
        "tash_salt",
        "test_algo",
        params);
    // check just created users
    assertEquals(user.id, fixtureUser.id);
    assertEquals(user.firstName, fixtureUser.firstName);
  }

  @Test
  public void testConfirmEmail() throws CubException {
    String invalidConfirmToken = "invalid";
    try {
      User.confirmEmail(invalidConfirmToken, new Params(Cub.apiKey));
      fail("exception excpected");
    } catch (BadRequestException e) {
      assertEquals("Bad token", e.getApiError().description);
    }
  }

  @Test
  public void testGetUserByRestorePasswordToken() throws CubException {
    String invalidRestorePasswordToken = "invalid";
    try {
      User.getUserByRestorePasswordToken(invalidRestorePasswordToken, new Params(Cub.apiKey));
    } catch (UnauthorizedException e) {
      assertEquals(e.getApiError().description,"Invalid token");
    }
  }

  @Test
  public void testResetPasswordWithToken() throws CubException {
    String invalidRestorePasswordToken = "invalid";
    try {
      User.resetPasswordWithToken(
          invalidRestorePasswordToken, "new password", new Params(Cub.apiKey));
    } catch (UnauthorizedException e) {
      assertEquals(e.getApiError().description,"Invalid token");
    }
  }
  
  @Test
  public void testLoginByTokenSuccess() throws CubException {
    User fixtureUser = (User) Cub.factory.fromString(getFixture("user"));
    String apiKey = "apiKey";
    String token = "temporary_token";
    String endpoint = String.format("/%s/login/%s", User.classUrl, token);
    mockPostToListEndpoint(endpoint, 200, "user", apiKey);
    User user = User.loginByToken(token, new Params(apiKey));
    assertEquals("token", user.getApiKey());
    assertEquals(user.id, fixtureUser.id);
    assertEquals(user.firstName, fixtureUser.firstName);
  }
  
  @Test(expected = UnauthorizedException.class)
  public void testLoginByTokenFailure() throws CubException {
    String apiKey = "api_key";
    String token = "temporary_token";
    String endpoint = String.format("/%s/login/%s", User.classUrl, token);
    mockPostToListEndpoint(endpoint, 401, "unautorized_error_invalid_token", apiKey);
    User.loginByToken(token, new Params(apiKey));
  }
  
  @Test
  public void testSetPasswordSuccess() throws CubException {
    String currentPassword = "currentUserPassword";
    String newPassword = "newUserPassword";
    String userToken = "userToken";
    
    User fixtureUser = (User) Cub.factory.fromString(getFixture("user"));
    String endpoint = String.format("/%s/password/", User.classUrl);
    mockPostToListEndpoint(endpoint, 200, "user", userToken);
  
    User userAfter = User.setPassword(currentPassword, newPassword, new Params(userToken));
    
    assertEquals(userAfter.id, fixtureUser.id);
  }
  
  @Test
  public void testLookupWithSsoOptions() throws CubException {
    String email = "any@email.com";
    String token = "token";
    String endpoint = String.format("/%s/lookup/", User.classUrl);
    mockPostToListEndpoint(endpoint, 200, "sso_options_exists", token);
    List<SsoOption> options = User.lookup(email, new Params(token));
    assertEquals(2, options.size());
  }
  
  @Test
  public void testLoookupWithoutSsoOptions() throws CubException {
    String email = "any@email.com";
    String token = "token";
    String endpoint = String.format("/%s/lookup/", User.classUrl);
    mockPostToListEndpoint(endpoint, 200, "sso_without_options", token);
    List<SsoOption> options = User.lookup(email, new Params(token));
    assertEquals(0, options.size());
  }
  
  @Test(expected = LookupAccountNotFoundException.class)
  public void testLookupValidationError() throws CubException {
    String email = "nootexist";
    String token = "token";
    String endpoint = String.format("/%s/lookup/", User.classUrl);
    mockPostToListEndpoint(endpoint, 404, "lookup_account_not_found", token);
    List<SsoOption> options = User.lookup(email, new Params(token));
  }
  
  @Test
  public void testSetPasswordInvalidCurrentPassword() throws CubException {
    String token = "userToken";
    String endpoint = String.format("/%s/password/", User.classUrl);
    mockPostToListEndpoint(endpoint, 400, "set_password_error", token);
  
    try {
      User.setPassword("invalidCurrentPassword", "anyNewPass", new Params(token));
    } catch (BadRequestException e) {
      assertEquals(400, (int) e.getApiError().code);
      assertTrue(e.getApiError().params.containsKey("password"));
      assertEquals(
          e.getApiError().params.get("password"),
          "Your password was entered incorrectly. Please enter it again.");
    }
  }
}
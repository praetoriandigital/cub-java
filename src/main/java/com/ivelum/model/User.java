package com.ivelum.model;

import com.google.gson.JsonElement;
import com.ivelum.Cub;
import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.CubResponse;
import com.ivelum.net.Params;
import com.ivelum.net.Transport;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class User extends ApiResource {
  public static final String classUrl = "user";
  public static final List<String> serializationIgnoreFields = new LinkedList<>(
          Arrays.asList("token"));

  public Date birthDate;
  public Date dateJoined;
  public String email;
  public Boolean emailConfirmed;
  public Boolean emailSetRequired;
  public String firstName;
  public String gender;
  public boolean invalidEmail;
  public Date invitationLastSentOn;
  public Integer invitationSentCount;
  public Date lastLogin;
  public String lastName;
  public String middleName;
  public String originalUsername;
  public Boolean passwordChangeRequired;
  public String photoLarge;
  public String photoSmall;
  public Boolean retired;
  public String token;
  public String username;
  public Boolean purchasingRoleBuyForOrganization;
  public Boolean purchasingRoleBuyForSelfOnly;
  public Boolean purchasingRoleRecommend;
  public Boolean purchasingRoleSpecifyForOrganization;
  public List<String> verifiedTags;
  public List<ExpandableField<Member>> membership;
  public ExpandableField<Site> registrationSite;

  /**
   * Login user with passed credentials.
   * @param username Username to login with
   * @param password Password to login with
   * @param site Uid of site where user is logging in
   * @return Logged in user object.
   * @throws CubException Base api exception
   */
  public static User login(String username, String password, String site) throws CubException {
    Params params = new Params();
    params.setValue("username", username);
    params.setValue("password", password);
    if (site != null) {
      params.setValue("site", site);
    }
  
    return (User) ApiResource.postApi(String.format("/%s/login", classUrl), params);
  }
  
  /**
   * Login user with passed credentials.
   * @param username Username to login with
   * @param password Password to login with
   * @return Logged in user object.
   * @throws CubException Base api exception
   */
  public static User login(String username, String password) throws CubException {
    return login(username, password, null);
  }
  
  /**
   * Registers user using default api key
   * @param firstName new user first name
   * @param lastName new user last name
   * @param email new user email
   * @param passwd new user password
   * @param registrationSite uid of site in the cub
   * @return User object registered
   * @throws CubException Network error or registration error
   */
  public static User register(
      String firstName, String lastName, String email, String passwd, String registrationSite
  ) throws CubException {
    Params params = new Params(Cub.apiKey);
    return register(firstName, lastName, email, passwd, registrationSite, params);
  }
  
  /**
   * Registers user
   * @param firstName new user first name
   * @param lastName new user last name
   * @param email new user email
   * @param passwd new user password
   * @param registrationSite uid of site in the cub
   * @param params the params object with the api key
   * @return User object registered
   * @throws CubException Network error or registration error
   */
  public static User register(
          String firstName, String lastName, String email, String passwd, String registrationSite,
          Params params) throws CubException {
    params.setValue("first_name", firstName);
    params.setValue("last_name", lastName);
    params.setValue("email", email);
    params.setValue("password", passwd);
    params.setValue("registration_site", registrationSite);

    return (User) ApiResource.postApi(String.format("/%s/register", classUrl), params);
  }
  
  /**
   * Registers user without password. User will recieve link to complete registration.
   * @param firstName new user first name
   * @param lastName new user last name
   * @param email new user email
   * @param registrationSite uid of site in the cub
   * @param params the params object with the api key, can be used to specify more user attrs,
   *               like middle_name or gender
   * @return User object registered
   * @throws CubException Network error or registration error
   */
  public static User registerWithoutPassword(
      String firstName, String lastName, String email, String registrationSite, Params params
  ) throws CubException {
    params.setValue("first_name", firstName);
    params.setValue("last_name", lastName);
    params.setValue("email", email);
    params.setValue("registration_site", registrationSite);
  
    String endPoint = String.format("/%s/register-without-password", classUrl);
    return (User) ApiResource.postApi(endPoint, params);
  }

  /**
   * Sends restore password for email
   * @param email to send restore password link
   * @param notificationSite the website uid
   * @return result of restore password as boolean
   * @throws CubException BadRequestException in case of invalid data
   */
  public static boolean sendRestorePasswordEmail(
          String email, String notificationSite) throws CubException {
    Params params = new Params();
    params.setValue("email", email);
    params.setValue("notification_site", notificationSite);

    String endpoint = String.format("/%s/forgot-password", classUrl);

    CubResponse resp = Transport.post(endpoint, params);

    JsonElement el = Cub.factory.parse(resp.getBody());
    return el.getAsJsonObject().get("result").getAsString().equals("password_reset_sent");
  }

  /**
   * Reissue token and returns user with new token. Will use default app api key from Cub.apiKey
   * @param token old user token
   * @return User with new token
   * @throws CubException BadRequestException in case of invalid data.
   */
  public static User getUserAndReissueToken(String token) throws CubException {
    return getUserAndReissueToken(token, Cub.apiKey);
  }

  /**
   * Reissue token and returns user with new token.
   * @param token an old user token to reissue
   * @param apiKey application api key you want to use
   * @return User with new token
   * @throws CubException BadRequestException in case of invalid data.
   */
  public static User getUserAndReissueToken(String token, String apiKey) throws CubException {
    Params params = new Params(token);
    params.setValue("app_key", apiKey);
    return (User) ApiResource.postApi(String.format("/%s/reissue-token", User.classUrl), params);
  }

  /**
   * Updates user username
   * @param newUsername a new username
   * @param password user password
   * @param params params with the apiKey
   * @return user model with the updated data
   * @throws CubException BadRequestException in case of invalid data.
   */
  public static User updateUsername(String newUsername, String password, Params params)
          throws CubException {
    params.setValue("username", newUsername);
    params.setValue("password", password);
    return (User) ApiResource.postApi(String.format("/%s/username", User.classUrl), params);
  }

  /**
   * Updates user email
   * @param newEmail a new emai
   * @param password user password
   * @param notificationSite uid of the site
   * @param params params with the apiKey
   * @return user model with the updated data
   * @throws CubException BadRequestException ins case of invalid data
   */
  public static User updateEmail(
          String newEmail, String password, String notificationSite, Params params)
          throws CubException {
    params.setValue("email", newEmail);
    params.setValue("password", password);
    params.setValue("notification_site", notificationSite);
    return (User) ApiResource.postApi(String.format("/%s/email", User.classUrl), params);
  }

  /**
   * Confirms user email
   * @param confirmEmailToken email confirmation token
   * @param params with application key
   * @return user object with confirmed email and user token
   * @throws CubException BadRequestException for invalid token, AccessDeniedException
   * for already confirmed email
   */
  public static User confirmEmail(String confirmEmailToken, Params params) throws CubException {
    return (User) ApiResource.postApi(
            String.format("/%s/confirm-email/%s", User.classUrl, confirmEmailToken), params);
  }

  /**
   * Allows reterive user by restore password token
   * @param restorePasswordToken restore password token from email
   * @param params params with the application api key
   * @return user object without user token
   * @throws CubException UnauthorizedException for invalid token
   */
  public static User getUserByRestorePasswordToken(
          String restorePasswordToken, Params params) throws CubException {
    String url = String.format("/%s/reset-password/%s", User.classUrl, restorePasswordToken);
    return (User) ApiResource.getApi(url, params);
  }

  /**
   * Set up new user password using restore password token
   * @param restorePasswordToken restore password token from email
   * @param newPassword new user password
   * @param params params with the application api key
   * @return User object with user token
   * @throws CubException UnauthorizedException for invalid token, BadRequestException for
   * invalid data
   */
  public static User resetPasswordWithToken(
          String restorePasswordToken, String newPassword, Params params) throws CubException {
    String url = String.format("/%s/reset-password/%s", User.classUrl, restorePasswordToken);
    params.setValue("new_password", newPassword);
    return (User) ApiResource.postApi(url, params);
  }
  
  /**
   * Logs user in using temporary token (not a JWT token)
   * @param token temporary token
   * @param params extra request params
   * @return logged user instance
   * @throws CubException usually UnauthorizedException for invalid token
   */
  public static User loginByToken(String token, Params params) throws CubException {
    String url = String.format("/%s/login/%s", User.classUrl, token);
    return (User) ApiResource.postApi(url, params);
  }
  
  /**
   * Allows to update user password, the Params api key must be user token
   * @param oldPwd current user password
   * @param newPwd new user password
   * @param params params with user token as api key
   * @return update user instance
   * @throws CubException In case of invalid current password BadRequestException will be thrown.
   */
  public static User setPassword(String oldPwd, String newPwd, Params params) throws CubException {
    String url = String.format("/%s/password/", User.classUrl);
    params.setValue("new_password", newPwd);
    params.setValue("password", oldPwd);
    return (User) ApiResource.postApi(url, params);
  }

  public static User get(String id) throws CubException {
    return get(id, new Params(Cub.apiKey));
  }

  public static User get(String id, Params params) throws CubException {
    return (User) get(id, User.class, params);
  }

  @Override
  public void setApiKey(String apiKey) {
    super.setApiKey(this.token == null ? apiKey : this.token);
  }
}

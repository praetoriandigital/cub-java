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
   * @return Logged in user object.
   * @throws CubException Base api exception
   */
  public static User login(String username, String password) throws CubException {

    String endpoint = String.format("/%s/login", classUrl);
    Params params = new Params();

    params.setValue("username", username);
    params.setValue("password", password);

    CubResponse resp = Transport.post(endpoint, params);
    return (User) Cub.factory.fromString(resp.getBody());
  }

  /**
   * Registers user
   * @param firstName new user first name
   * @param lastName new user last name
   * @param email new user email
   * @param passwd new user password
   * @param registrationSite uid of site in the cub
   * @return User object registered
   * @throws CubException Network error or registration error
   */
  public static User register(
          String firstName, String lastName, String email, String passwd, String registrationSite)
          throws CubException {
    String endpoint = String.format("/%s/register", classUrl);
    Params params = new Params();
    params.setValue("first_name", firstName);
    params.setValue("last_name", lastName);
    params.setValue("email", email);
    params.setValue("password", passwd);
    params.setValue("registration_site", registrationSite);

    CubResponse resp = Transport.post(endpoint, params);
    return (User) Cub.factory.fromString(resp.getBody());
  }

  /**
   * Sends restore password for email
   * @param email to send restore password link
   * @param notificationSite the website uid
   * @return result of restore password as boolean
   * @throws CubException BadRequestException in case of invalid data
   */
  public static boolean restorePassword(String email, String notificationSite) throws CubException {
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

    String endpoint = String.format("/%s/reissue-token", User.classUrl);
    CubResponse resp = Transport.post(endpoint, params);

    return (User) Cub.factory.fromString(resp.getBody());
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
    String endpoint = String.format("/%s/username", User.classUrl);
    CubResponse resp = Transport.post(endpoint, params);

    return (User) Cub.factory.fromString(resp.getBody());
  }


  public static User get(String id, Params params) throws CubException {
    return (User) get(id, User.class, params);
  }

  @Override
  public void setApiKey(String apiKey) {
    super.setApiKey(this.token == null ? apiKey : this.token);
  }
}

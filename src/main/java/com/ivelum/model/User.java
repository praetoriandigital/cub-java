package com.ivelum.model;

import com.ivelum.Cub;
import com.ivelum.exception.CubException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.CubResponse;
import com.ivelum.net.Params;
import com.ivelum.net.Transport;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class User extends ApiResource {
  public static final String classUrl = "user";
  public static final List<String> serializationIgnoreFields = new LinkedList<String>(
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
   * @throws UnsupportedEncodingException Encode credentials error.
   */
  public static User login(String username, String password)
          throws CubException, UnsupportedEncodingException {

    String endpoint = String.format("/%s/login", classUrl);
    Params params = new Params();

    params.setValue("username", username);
    params.setValue("password", password);

    CubResponse resp = Transport.post(endpoint, params);
    return (User) Cub.factory.fromString(resp.getBody());
  }

  public static User get(String id, Params params)
          throws CubException, UnsupportedEncodingException {

    return (User) get(id, User.class, params);
  }

  @Override
  public void setApiKey(String apiKey) {
    super.setApiKey(this.token == null ? apiKey : this.token);
  }
}

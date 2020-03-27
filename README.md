# Cub Client for Java [![Build Status](https://travis-ci.org/praetoriandigital/cub-java.svg?branch=master)](https://travis-ci.org/praetoriandigital/cub-java)

Cub is our system for user authorization and management. 

It provides an API for data access and the [cub widget](https://github.com/praetoriandigital/cub-docs) that implements 
UI for most common user functionalities. 

The integration with Cub can be done:
* with direct API usage. 
* with [cub widget](https://github.com/praetoriandigital/cub-docs) integration.  It allows the use of already implemented functionalities for user registration/login/logout/profile data and more.
                                                                                
                                                                                
In both cases, a webhook endpoint needs to be set up to keep data in sync.


## Requirements

Java 1.8 or later.

## Installation

### Gradle users

You can use JCenter: 
```groovy 
repositories {
    jcenter()
}
```

Or bintray repository: 
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/ivelum/cub-java/"
    }
}
```
Add this dependency to your project's build file:

```groovy
     compile "com.ivelum:cub-java:0.14.0"
```

### Maven users

You can use JCentral repository:
```xml
    <repositories>
        <repository>
            <id>jcenter</id>
            <url>https://jcenter.bintray.com/</url>
        </repository>
    </repositories>
```


Or bintray repository:
```xml
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-ivelum-cub-java</id>
        <name>bintray</name>
        <url>https://dl.bintray.com/ivelum/cub-java</url>
    </repository>
```

Add this dependency to your project's POM:
```xml 
    <dependency>
        <groupId>com.ivelum</groupId>
        <artifactId>cub-java</artifactId>
        <version>0.14.0</version>
    </dependency>
```

## Usage

### Webhooks processing 

When something changes in the User model (or any other model that you are interested in) Cub will send the latest data for the model that was updated to the endpoint. But webhooks on their own are not reliable, and should be used in conjunction with the API, because:
- a webhook can contain outdated data (for example, on retries after a failed request) that would lead to a race condition;
- some webhooks can be lost, and even the most reliable systems occasionally fail;
- misconfigurations can cause webhooks to be sent unintentionally from stage environments 

So, webhooks should be considered as signals to pull the latest data through API.

```java

public class WebhookProcessor {
  /**
   * Process webhook body and returns HTTP status for a response.
   * 
   * @param hookBody webhook HTTP request body
   * @return HttpResponse status code
   */
  public int processWebhookData(String hookBody) {
    int httpStatusOk = 200;
    int httpStatusError = 500;
    CubObject obj;

    try {
      obj = Cub.factory.fromString(hookBody);
    } catch (DeserializationUnknownCubModelException e) {
      // Webhook related to the cub model that was not implemented in the cub-java. 
      return httpStatusOk;
    } catch (DeserializationException e) {
      // Cub-java can not deserialize webhook data to the Cub model. 
      return httpStatusError;
    }

    if (obj.deleted != null && obj.deleted) {
      // Webhook for deleted object in cub.
      try {
        ((ApiResource) obj).reload();
      } catch (NotFoundException e) {
        // @todo: Object was deleted in cub. Process it.
        return httpStatusOk;
      } catch (CubException e) {
        // Something went wrong.
        return httpStatusError;
      }

      // Webhook about object deletion arrived, but object still present in Cub.
      // @todo: Related model(s) must be updated in the system.
      return httpStatusOk; // status 200 if model(s) were update, otherwise 500 status.
    }

    // Object modified in Cub.
    try {
      ((ApiResource) obj).reload();
    } catch (AccessDeniedException e) {
      // @todo: Process it
      //    access denied for the object. Application lost access to it.
      //    For example user can be deactivated in the cub, or connection with the
      //    application was removed.
      //    this object can not be synchronized anymore.
      return httpStatusOk;
    } catch (CubException e) {
      // error reloading object
      return httpStatusError;
    }

    // Object reloaded without error, now we have the latest snapshot of a model from Cub.
    // @todo: Related model(s) must be updated in the system.
    return httpStatusOk; // status 200 if model(s) were update, otherwise 500 status.
  }
}
```

### API for login and user tokens.

#### Check SSO options before showing login form. 

Some users are required to use SSO for login. 
Use lookup API to find SSO options available for the user.

```java 
  public void checkSsoOptions() throws CubException {
    String emailOrUsername = "any@email.com";
    String token = "public_key";
    try { 
        List<SsoOption> options = User.lookup(email, new Params(token));
    } catch (LookupAccountNotFoundException e) {
        // requested account was not founds
    } 
    if (options.size() == 0) {
        // no any sso option set up for user, use regular login
    } else {
        // show sso options insead of regular login form. 
    } 
  }
```

To start SP-initiated login application must:
1) Use URL from SsoOption object
2) Add site get parameter with site UID value to this URL 
3) Redirect the user to created URL (or just put a link on a page)

For example 
Url from SsoOption: ```https://id.lexipol.com/sso/saml2/spif_login/saml2_xxx```
End-user URL on the site ```https://id.lexipol.com/sso/saml2/spif_login/saml2_xxx?site=ste_xxxx```
Where ```ste_xxxx``` is the site UID where user needs to be logged. 


### Password based login and tokens
User tokens are  [JWT tokens](https://jwt.io). You can verify a token using the application secret key.
 
```java
  import com.ivelum.model.CubObject;
  import com.ivelum.model.State;
  import com.ivelum.model.User;
  import com.ivelum.Cub;
  

  public class CubLoginExample {
    public static void main(String[] args) {
      // setup ApiKey 
      Cub.apiKey = "your api key";
      
      // login
      try {
        User user = User.login("username", "password");
      } catch (BadRequestException e) {
        // Process api error response with e.getApiError();
      } catch (CubException e) {
        // Process other exception
        return;
      }
      
      // Retrieve user jwt token. 
      String userToken = user.getApiKey();
    }
  }
```

Error handling for the login errors is similar to the handling BadRequest errors. The getApiError()
method of exception will return com.ivelum.model.ApiError object. The description field of the ApiError 
object will have user-friendly message. 

Most common cases:
* Login and password are invalid. Or user was disabled. See tests folder com.ivelum.model.UserTest.java::testLoginFailed method. 
* Login and password are valid, but LexipolID require user to update password after login. See tests com.ivelum.model.UserTest.java::testLoginPasswordChangeRequiredWithoutSite 
and com.ivelum.model.UserTest.java::testLoginPasswordChangeRequiredWithSite methods 
* Login and password are valid, but LexipolID require user to update password using email after login. See tests com.ivelum.model.UserTest.java::testLoginPasswordChangeByEmailRequiredWithSite 
and com.ivelum.model.UserTest.java::testLoginPasswordChangeByEmailRequiredWithoutSite methods 
* User inactive (banned) on site see com.ivelum.model.UserTest.java::testLoginForInactiveUser. 
This situation is possible only if you passed site to log in into User.login method. 

### Howto renew user JWT token

```java
public class CubTokenRenewExample {
  public void example() {     
    String token = "xxx"; // User JWT token, not an secret or public API key.
    String userId = "uid_1";
    User user = User.get(userId, new Params(token));
    // User response may contain token field
    assertNull(user.token); // The used token is valid, and the expiration date is far away from now. No token returned.
    // After some time. About a half of token time life. 
    user = User.get(user.id, new Params(token));
    assertNotNull(user.token); // The token is near to expiration date, so new token was returned
    assertNotEquals(user.token, token); // Now you have the new token, and you can update your cookie or do whatever you want.
  } 
}
```
### Handling BadRequestException

Each com.ivelum.exception.BadRequestException has the getApiError method that provides an ApiError object with more information about the error. 
See the ApiError definition for more details 

### Update user profile

```java
  import com.ivelum.model.ApiError;
  import com.ivelum.model.CubObject;
  import com.ivelum.model.State;
  import com.ivelum.model.User;
  import com.ivelum.Cub;
  

  public class CubUpdateProfileExample {
    public static void main(String[] args) {
      // setup ApiKey 
      Cub.apiKey = "your api key";

      // Use User's JWT Token recieved with login method to retrieve user
      user = User.get("user_id", new Params("user token retrieved with login"));
      
      try {
        user = User.get("usr_upfrcJvCTyXCVBj8", new Params("user jwt token"));
      } catch (CubException e) {
        return;
      }
  
      user.middleName = "new user middle name";
  
      try {
        user.save();
      } catch (CubException e) {
        return;
      }
  
      // Retrieve user copy using user token that stored in the user.getApiKey() after login
      User userCopy;
      try {
        userCopy = User.get(user.id, new Params(user.getApiKey()));
      } catch (BadRequestException e) {
        ApiError error = e.getApiError();
        // error.description contains general description errors. 
        // error.params could have more detailed errors for each field. 
      } catch (CubException e) {
        return;
      }
  
      assert user.middleName.equals(userCopy.middleName);
    }
  }
```

### Create objects 

```java 

  class CubCreateExample {
    public static void main(String[] args) {
      Cub.apiKey = "Api key with permissions to create group ";
  
      String orgId = "organization id to create group";
  
      Group group = new Group();
      group.organization = new ExpandableField<>(orgId, null);
  
  
      assert group.id == null;
  
      try {
        group.save();
      } catch (BadRequestException e) {
        // validation error. Missing name field. 
        ApiError apiError = e.getApiError();
        assert apiError.params.get("name").contains("required");
      } catch (CubException e) {
        // unexpected error 
      }
  
      group.name = "new group";
      try {
        group.save();
      } catch (CubException e) {
        // unexpected error
      }
  
      // group was created, and we have fresh instance from the API. 
      assert group.id != null;
    }
  }
  
```

### Expandable objects. 

```java
  import com.ivelum.model.CubObject;
  import com.ivelum.model.State;
  import com.ivelum.model.User;
  import com.ivelum.Cub;
  

  public class CubExample {
    public static void main(String[] args) {
      // setup ApiKey 
      Cub.apiKey = "your api key";

      // Search states with the default API key, default offset/count 
      
      states = State.list();
      
      Params params = new Params("another api key or null to use default");
      params.setCount(10).setOffset(5);
      params.setValue("order_by", "-name"); // order by name (reversed)
      states = State.list(params);
      State state = (State) states.get(0);
      
      // The country variable is a reference to another object.
      // It has the id of its related country object, and may have an expanded country object.
      assert state.country.getId() != null; // It has country id
      assert state.country.getExpanded() == null; // we don't have expanded country object
  
      // Get states with already expanded country objects.
      params = new Params();
  
      // Tells Cub to return states with expanded country objects, not ids only.
      params.setExpands("country");
      params.setValue("order_by", "-name"); // set order
      states = State.list(params);
  
      state = (State) states.get(0);
  
      // Checks that country was expanded
      assert state.country.getId() != null;
      Country country = state.country.getExpanded();
      assert country.name != null;
    }
  }
```    
## Iterate over API 

```java
import com.ivelum.model.CubObject;
import com.ivelum.model.Country;
  

public class CubExample {
    public static void main(String[] args) {
        int offset = 0;
        int count = 100;
      
        Params params = new Params();
        params.setCount(count);
      
        List<CubObject> result;
        do {
          result = Country.list(params);
          params.setOffset(offset += count);
          for (CubObject item: result ) {
            Country country = (Country) item;
            // your code ... 
          }
        } while (result.size() > 0);
    }
}
```

## Report bugs

Report issues to the project's [issues tracking](https://github.com/praetoriandigital/cub-java/issues) on Github.

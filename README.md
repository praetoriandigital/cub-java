# Cub Client for Java [![Build Status](https://travis-ci.org/praetoriandigital/cub-java.svg?branch=master)](https://travis-ci.org/praetoriandigital/cub-java)

Cub OneSource is our system for user authorization and management. 

It provides an API for data access and the [cub widget](https://github.com/praetoriandigital/cub-docs) that implements 
UI for most common user functionalities. 

The integration with Cub can be done:
* with direct API usage. 
* with [cub widget](https://github.com/praetoriandigital/cub-docs) integration.  It allows using already implemented functionality about user registration/login/logout/profile data and more.
                                                                                
                                                                                
In both cases, you have set up webhook endpoint to keep data in sync.


## Requirements

Java 1.8 or later.

## Installation

### Gradle users

Repository: 
```groovy
    maven {
        url  "https://dl.bintray.com/ivelum/cub-java/"
    }
```
Add this dependency to your project's build file:

```groovy
     compile "com.ivelum:cub-java:0.2.1"
```

### Maven users

Repository:
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
        <version>0.2.1</version>
    </dependency>
```

## Usage

### Webhooks processing 

When something changes in User model (or any other Model that you are interested in) Cub will send the latest data for the model that was updated to the endpoint. But only webhooks 
are not reliable and should be used together with API, because:
- there can be a race condition between webhooks when webhook can contain outdated data(for example, on retries after failed requests);
- some webhooks can be lost, even the most reliable systems sometimes fail;
- webhooks can be sent unintentionally from stage environments (because of misconfigurations)

So, webhooks should be considered as signals to pull the latest data through API.

```java

public class WebhookProcessor {
  /**
   * Process webhook body returns HTTP status for a response.
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

      // Retrieving user from the server using user JWT token received with login method. 
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
        // validation error. Missed name field. 
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
      // It has an id of the related country object. And may have a full country object.
      assert state.country.getId() != null; // It has country id
      assert state.country.getExpanded() == null; // we dont have related country object
  
      // Get states with already populated country objects.
      params = new Params();
  
      // Tells Cub to return states with a full country object, not id only.
      params.setExpands("country");
      params.setValue("order_by", "-name"); // set order
      states = State.list(params);
  
      state = (State) states.get(0);
  
      // Checks that country was populated
      assert state.country.getId() != null;
      Country country = state.country.getExpanded();
      assert country.name != null;
    }
  }
```    
    
## Report bugs

Report issues to the project's [issues tracking](https://github.com/praetoriandigital/cub-java/issues) on Github.

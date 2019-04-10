#Cub Client for Java [![Build Status](https://travis-ci.org/praetoriandigital/cub-java.svg?branch=master)](https://travis-ci.org/praetoriandigital/cub-java)

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
     compile "com.ivelum:cub-java:0.1.2"
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
        <version>0.1.2</version>
    </dependency>
```

## Usage

### Webhooks processing 

When something changes in User model(or any other Model that you are interested in) Cub 
will send latest data for the model that was updated to the endpoint. But only webhooks 
are not reliable and should be used together with API, because:
- there can be a race condition between webhooks when webhook can contain outdated data(for example, on retries after failed requests);
- some webhooks can be lost, even the most reliable systems sometimes fail;
- webhooks can be sent unintentionally from stage environments (because of misconfigurations)

So, webhooks should be considered as signals to pull the latest data through API.

```java

public class WebhookProcessor {
  /**
   * Process webhook body, returns http status for response.
   * 
   * @param hookBody webhook http request body
   * @return HttpResponse status code
   */
  public int processWebhookData(String hookBody) {
    int httpStatusOk = 200;
    int httpStatusError = 500;
    CubObject obj;

    try {
      obj = Cub.factory.fromString(hookBody);
    } catch (DeserializationUnknownCubModelException e) {
      // Webhook related to the cub model which was not implemented in the cub-java. 
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
      } catch (CubException | UnsupportedEncodingException e) {
        // Something went wrong.
        return httpStatusError;
      }

      // Webhook about object deletion arrived, but actually object still present in Cub.
      // @todo: Related model(s) must be updated in the system.
      return httpStatusOk; // status 200 if model(s) were update, otherwise 500 status.
    }

    // Object modified in Cub.
    try {
      ((ApiResource) obj).reload();
    } catch (AccessDeniedException e) {
      // @todo: Process it
      //    access denied for the object. Application lost access to it.
      //    for example users can be deactivated in the cub, or connection with the
      //    application was removed.
      //    this object can not be synchronized anymore.
      return httpStatusOk;
    } catch (CubException | UnsupportedEncodingException e) {
      // error reloading object
      return httpStatusError;
    }

    // object reloaded without error, now we have latest snapshot of model from cub.
    // @todo: Related model(s) must be updated in the system.
    return httpStatusOk; // status 200 if model(s) were update, otherwise 500 status.
  }
}
```


### API for login and user tokens.

User tokens are  [JWT tokens](https://jwt.io). That can be verified using application secret key.
 
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
      User user = User.login("username", "password");
      
      // Retrieve user jwt token. 
      String userToken = user.getApiKey();
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

      // Search states with default api key, default offset/count 
      
      states = State.list();
      
      Params params = new Params("another api key or null to use default");
      params.setCount(10).setOffset(5);
      params.setValue("order_by", "-name"); // order by name (reversed)
      states = State.list(params);
      State state = (State) states.get(0);
      
      // The country variable is reference object.
      // It has id of related country object. And may have full country object.
      assert state.country.getId() != null; // It has country id
      assert state.country.getExpanded() == null; // we dont have related country object
  
      // Get states with already populated country objects.
      params = new Params();
  
      // Tells cub to return states with full country object, not id only.
      params.setExpands("country");
      params.setValue("order_by", "-name"); // set order
      states = State.list(params);
  
      state = (State) states.get(0);
  
      // check country is already populated
      assert state.country.getId() != null;
      Country country = state.country.getExpanded();
      assert country.name != null;
    }
  }
```    
    
##Report bugs

Report issues to the project's [issues tracking](https://github.com/praetoriandigital/cub-java/issues) on Github.


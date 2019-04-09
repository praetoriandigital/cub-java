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
     compile "com.ivelum:cub-java:0.1.1"
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
        <version>0.1.1</version>
    </dependency>
```

## Usage

```java
  import com.ivelum.model.CubObject;
  import com.ivelum.model.State;
  import com.ivelum.model.User;
  import com.ivelum.Cub;
  

  public class CubExample {
    public static void main(String[] args) {
      // setup ApiKey 
      Cub.apiKey = "your api key";
      
      // login
      User user = User.login("username", "password");
      
      String userToken = user.getApiKey();
      
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


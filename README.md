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
     compile "com.ivelum:cub-java:0.1.0"
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
        <version>0.1.0</version>
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
    }
  }
```
    
    
##Report bugs

Report issues to the project's [issues tracking](https://github.com/praetoriandigital/cub-java/issues) on Github.


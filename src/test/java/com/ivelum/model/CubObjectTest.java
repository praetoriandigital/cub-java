package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.annotations.SerializedName;
import com.ivelum.exception.InvalidRequestException;
import com.ivelum.net.Params;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;


public class CubObjectTest {
  @Test
  public void testFieldsToParams() throws InvalidRequestException {

    class Nested extends CubObject {
      public String name;
    }

    class Tested extends CubObject {
      public String name;
      public Integer price;
      public String camelCase;
      public Boolean nullValue;
      @SerializedName("serialized")
      public String serializedName;

      public ExpandableField<Nested> nested;
      public List<ExpandableField<Nested>> nesteds;
    }

    Tested t = new Tested();
    t.name = "hello";
    t.price = 123;
    t.camelCase = "hello";
    t.serializedName = "serializedName";


    Nested nested = new Nested();
    nested.id = "123";
    nested.name = "name";

    t.nested = new ExpandableField<>(nested.id, nested);
    t.nesteds = new LinkedList<>();
    t.nesteds.add(new ExpandableField<>(nested.id, nested));

    Params params = new Params();
    t.toParams(params);
    Map<String, Object> paramValues = params.toMap();

    assertTrue(paramValues.containsKey("name"));
    assertEquals(paramValues.get("name"), t.name);
    assertTrue(paramValues.containsKey("price"));
    assertEquals(paramValues.get("price"), t.price);

    assertTrue(paramValues.containsKey("camel_case"));
    assertEquals(paramValues.get("camel_case"), t.camelCase);

    assertTrue(paramValues.containsKey("null_value"));
    assertEquals(paramValues.get("null_value"), null);
    assertTrue(paramValues.containsKey("serialized"));
    assertEquals(paramValues.get("serialized"), t.serializedName);

    assertTrue(paramValues.containsKey("nested"));
    assertEquals(paramValues.get("nested"), nested.id);

    assertTrue(paramValues.containsKey("nesteds"));
    assertEquals(paramValues.get("nesteds"), new LinkedList<String>(Arrays.asList(nested.id)));

    t.name = "";
    params = new Params();
    t.toParams(params);
    assertTrue(params.toMap().containsKey("name"));
    assertTrue(params.toMap().containsKey("price"));
    assertTrue(params.toMap().containsKey("camel_case"));
  }

  @Test
  public void testStaticFieldsAreNotSerialized() throws InvalidRequestException {
    User user = new User();
    Params params = new Params();
    user.toParams(params);
    Map<String, Object> paramValues = params.toMap();

    // static field must not be serialized
    assertFalse(paramValues.containsKey("class_url"));

  }
}

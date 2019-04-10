package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.exception.DeserializationException;
import com.ivelum.exception.DeserializationUnknownCubModelException;
import org.junit.Test;



public class FactoryTest extends CubModelBaseTest {

  static class Nested extends CubObject {
    String name;
    Tested tested;
  }

  static class Tested extends CubObject {
    String key;
    ExpandableField<Nested> nested;
  }

  @Test(expected = DeserializationException.class)
  public void testUnknownObject() throws CubException {
    String unknown = "{\"id\": \"unk_1\", \"object\": \"unsupported\"}";
    Cub.factory.fromString(unknown);
  }

  @Test(expected = DeserializationException.class)
  public void testInvalidJsonDeserialization() throws CubException {
    String unknown = "{\"id\": \"unk_1\"";
    Cub.factory.fromString(unknown);
  }

  @Test(expected = DeserializationException.class)
  public void testJsonWithoutObjectDeserialization() throws CubException {
    String unknown = "{\"id\": \"unk_1\"}";
    Cub.factory.fromString(unknown);
  }

  @Test
  public void testRegisterDeregister() {
    String jsonStr = "{\"id\": \"123\", \"key\": \"value\", \"object\": \"tested\"}";
    String objName = "tested";

    // Tested class is not registered and can not be deserealized
    try {
      Cub.factory.fromString(jsonStr);
      fail(); // expected deserialization exception
    } catch (DeserializationUnknownCubModelException e) {
      assertEquals(e.unknownCubModelName, "tested");
    } catch (DeserializationException ignore) {
      fail(); // unexpected
    }

    // Register and check deserialization
    Cub.factory.registerKnownClass(objName, Tested.class);

    try {
      Tested tested = (Tested) Cub.factory.fromString(jsonStr);
      assertEquals(tested.key, "value");
      assertNull(tested.getApiKey());
    } catch (DeserializationException e) {
      fail();// unexpected behaviour
    }

    // Check deregister removes known class
    Cub.factory.deregisterKnownClass(objName);
    try {
      Cub.factory.fromString(jsonStr);
      fail(); // expected deserialization exception
    } catch (DeserializationUnknownCubModelException e) {
      assertEquals(e.unknownCubModelName, "tested");
    } catch (DeserializationException ignore) {
      fail(); // unexpected
    }
  }

  @Test
  public void testApiKeyPassedToCreatedObject() throws DeserializationException {
    String apiKey = "123";
    String nestedData = "{\"id\": \"1\", \"name\": \"name\", \"object\": \"nested\"}";
    String data = String.format(
            "{\"key\": \"key\", \"object\": \"tested\", \"nested\": %s}",
            nestedData);

    Cub.factory.registerKnownClass("nested", Nested.class);
    Cub.factory.registerKnownClass("tested", Tested.class);

    Tested tested = (Tested) Cub.factory.fromString(data, apiKey);
    assertEquals("key", tested.key);
    assertEquals(apiKey, tested.getApiKey());
    assertEquals(apiKey, tested.nested.getExpanded().getApiKey());
  }

  @Test
  public void testReloadCreatesNewInstanceExceptFirstOne() {

    Cub.factory.registerKnownClass("nested", Nested.class);
    Cub.factory.registerKnownClass("tested", Tested.class);

    Tested tested = new Tested();
    tested.id = "any";
    tested.key = "any";
    tested.nested = null;
    int before = System.identityHashCode(tested);
    Cub.factory.updateFromString(
            getFixture("tested_nested_tested"),
            tested,
            new UpdateExistingInstanceCreator<Tested>(tested));

    assertEquals(before, System.identityHashCode(tested));
    assertEquals(tested.id, "tested_1");
    assertEquals(tested.key, "tested_1");

    Tested another = tested.nested.getExpanded().tested;

    assertNotEquals(before, System.identityHashCode(tested.nested.getExpanded()));
    assertEquals("tested_2", another.id);
    assertEquals("tested_2", another.key);
    assertNull(another.nested);

    Cub.factory.deregisterKnownClass("nested");
    Cub.factory.deregisterKnownClass("tested");

  }

  @Test
  public void testGetGson() {
    Gson gsonDefault = Cub.factory.getGson(null);
    Gson gsonForKey1 = Cub.factory.getGson("key1");
    assertNotEquals(gsonDefault, gsonForKey1);
    Gson gsonForKey2 = Cub.factory.getGson("key2");
    assertNotEquals(gsonForKey1, gsonForKey2);

    Gson gsonForKey1Copy = Cub.factory.getGson("key1");

    assertEquals(gsonForKey1, gsonForKey1Copy);
  }
}

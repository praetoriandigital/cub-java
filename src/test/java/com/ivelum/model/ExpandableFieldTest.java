package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.google.gson.JsonParseException;
import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.DeserializationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExpandableFieldTest extends CubModelBaseTest {

  private static class SimpleModel extends CubObject {
    static final String objectName = "simple";
    String name;
  }

  private static class ExpandableModel extends CubObject {
    static final String objectName = "expandable";
    @SuppressWarnings("unused")
    String name;

    ExpandableField<SimpleModel> nested;
  }

  @Before
  public void setUp() {
    Cub.factory.registerKnownClass(
            SimpleModel.objectName, SimpleModel.class);
    Cub.factory.registerKnownClass(
            ExpandableModel.objectName, ExpandableModel.class);
  }

  @After
  public void tearDown() {
    Cub.factory.deregisterKnownClass(SimpleModel.objectName);
    Cub.factory.deregisterKnownClass(ExpandableModel.objectName);
  }

  @Test
  public void testDeserializationWhenExpandableIsNull() throws DeserializationException {
    String expandableObjWithNull = "{\"name\": \"Oak\", \"nested\": null, "
            + "\"object\": \"expandable\"}";

    ExpandableModel model = (ExpandableModel) Cub.factory.fromString(expandableObjWithNull);
    assertNull(model.getApiKey());

    assertEquals("Oak", model.name);
    assertNull(model.nested);
  }

  @Test
  public void testDeserializationWhenExapandableIsStringId() throws DeserializationException {
    String expandableObjWithNull = "{\"name\": \"Oak\", \"nested\": \"id\", "
            + "\"object\": \"expandable\"}";

    ExpandableModel model = (ExpandableModel) Cub.factory.fromString(expandableObjWithNull);

    assertEquals("Oak", model.name);
    assertNotNull(model.nested);
    assertEquals("id", model.nested.getId());
  }

  @Test
  public void testDeserializerWhenExpandableIsObject() throws DeserializationException {
    String apiKey = "1";
    String nestedJsonStr = "{\"name\": \"nested\", \"object\": \"simple\", \"id\": \"id2\"}";
    String expandableObjWithNull = String.format(
            "{\"name\": \"Oak\", \"nested\": %s, \"object\": \"expandable\"}",
            nestedJsonStr);

    ExpandableModel model = (ExpandableModel) Cub.factory.fromString(expandableObjWithNull, apiKey);

    assertEquals("Oak", model.name);
    assertNotNull(model.nested);
    assertEquals("id2", model.nested.getId());
    SimpleModel sm = model.nested.getExpanded();
    assertEquals(apiKey, model.nested.getExpanded().getApiKey());
    assertEquals(apiKey, sm.getApiKey());
    assertEquals("nested", sm.name);
  }

  @Test(expected = JsonParseException.class)
  public void testDeserializerWhenExpandableIsInvalid() throws DeserializationException {
    String expandableObjWithNull = "{\"name\": \"Oak\", \"nested\": 123, \"object\": "
            + "\"expandable\"}";

    Cub.factory.fromString(expandableObjWithNull);
  }

  @Test(expected = JsonParseException.class)
  public void testDeserializerWhenExpandableWrongType() throws DeserializationException {
    String nestedJsonStr = "{\"id\": \"123\", \"object\": \"unknown\" }";
    String expandableObjWithNull = String.format(
            "{\"name\": \"Oak\", \"nested\": %s, \"object\": \"expandable\"}",
            nestedJsonStr);

    Cub.factory.fromString(expandableObjWithNull);
  }
}

package com.ivelum.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import com.ivelum.exception.DeserializationException;
import com.ivelum.exception.DeserializationUnknownCubModelException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Factory {


  private final JsonParser parser = new JsonParser();
  private Map<String, Gson> gsons = new HashMap<>();

  private final Map<String, Class> knownClasses = new HashMap<>();

  protected static FieldNamingPolicy namingPolicy = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

  public Gson getGsonForUpdate(CubObject instance, InstanceCreator<?> creator) {
    String apiKey = instance.getApiKey();
    Gson newGson = new GsonBuilder()
            .setFieldNamingPolicy(namingPolicy)
            .registerTypeAdapter(ExpandableField.class, new ExpandableFieldDeserializer(apiKey))
            .registerTypeAdapterFactory(new UpdateApiKeyAdapterFactory(apiKey))
            .registerTypeAdapter(instance.getClass(), creator)
            .create();
    return newGson;
  }

  public Gson getGson(String apiKey) {
    if (gsons.containsKey(apiKey)) {
      return gsons.get(apiKey);
    }

    Gson newGson = new GsonBuilder()
            .setFieldNamingPolicy(Factory.namingPolicy)
            .registerTypeAdapter(ExpandableField.class, new ExpandableFieldDeserializer(apiKey))
            .registerTypeAdapterFactory(new UpdateApiKeyAdapterFactory(apiKey))
            .create();

    gsons.put(apiKey, newGson);
    return newGson;
  }

  public Factory() {
    registerKnownClass("organization", Organization.class);
    registerKnownClass("state", State.class);
    registerKnownClass("country", Country.class);
    registerKnownClass("user", User.class);
    registerKnownClass("member", Member.class);
    registerKnownClass("memberposition", MemberPosition.class);
    registerKnownClass("groupmember", GroupMember.class);
    registerKnownClass("group", Group.class);
    registerKnownClass("site", Site.class);
    registerKnownClass("product", Product.class);
    registerKnownClass("sku", Sku.class);
    registerKnownClass("plan", Plan.class);
  }

  public void registerKnownClass(String name, Class cls) {
    knownClasses.put(name, cls);
  }

  public void deregisterKnownClass(String name) {
    knownClasses.remove(name);
  }

  /**
   * Parses string into JsonElement
   * @param data to be parsed
   * @return result as JsonElement
   */
  public JsonElement parse(String data) {
    return parser.parse(data);
  }


  public CubObject fromString(String jsonAsString) throws DeserializationException {
    return fromString(jsonAsString, null);
  }

  /**
   * Returns CubObject instance from string json
   * @param jsonAsString string with json
   * @param apiKey api key to inject into the result object
   * @return CubObject instance
   * @throws DeserializationException In case of invalid json or unknown class
   */
  public CubObject fromString(String jsonAsString, String apiKey) throws DeserializationException {
    try {
      JsonElement el = parser.parse(jsonAsString);
      return fromObject(el, apiKey);
    } catch (JsonSyntaxException e) {
      throw new DeserializationException(e.getMessage(), e);
    }
  }

  public CubObject fromObject(JsonElement obj) throws DeserializationException {
    return fromObject(obj, null);
  }

  public CubObject fromObject(JsonElement obj, String apiKey) throws DeserializationException {
    JsonElement objectElement = obj.getAsJsonObject().get("object");
    if (objectElement == null) {
      throw new DeserializationException("Missed object property in json");
    }
    String objectName = objectElement.getAsString();

    if (knownClasses.containsKey(objectName)) {
      return getGson(apiKey).fromJson(obj, (Type) knownClasses.get(objectName));
    }

    String errMsg = String.format("Unsupported object type '%s'", objectName);
    throw new DeserializationUnknownCubModelException(objectName, errMsg);
  }

  public void updateFromString(String jsonAsString, CubObject instance,
                               InstanceCreator<?> creator) {
    getGsonForUpdate(instance, creator).fromJson(jsonAsString, instance.getClass());
  }

  public static FieldNamingPolicy getFieldNamingPolicy() {
    return namingPolicy;
  }
}

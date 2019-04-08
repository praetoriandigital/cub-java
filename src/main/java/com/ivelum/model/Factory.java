package com.ivelum.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.ivelum.exception.DeserializationException;

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
  }

  public void registerKnownClass(String name, Class cls) {
    knownClasses.put(name, cls);
  }

  public void deregisterKnownClass(String name) {
    knownClasses.remove(name);
  }

  public CubObject fromString(String jsonAsString) throws DeserializationException {
    return fromString(jsonAsString, null);
  }

  public CubObject fromString(String jsonAsString, String apiKey) throws DeserializationException {
    JsonElement el = parser.parse(jsonAsString);
    return fromObject(el, apiKey);
  }

  public CubObject fromObject(JsonElement obj) throws DeserializationException {
    return fromObject(obj, null);
  }

  public CubObject fromObject(JsonElement obj, String apiKey) throws DeserializationException {
    // @todo: handle Null pointer exception
    String objectName = obj.getAsJsonObject().get("object").getAsString();

    if (knownClasses.containsKey(objectName)) {
      return getGson(apiKey).fromJson(obj, (Type) knownClasses.get(objectName));
    }

    String errMsg = String.format("Unsupported object type '%s'", objectName);
    throw new DeserializationException(errMsg);
  }

  public void updateFromString(String jsonAsString, CubObject instance,
                               InstanceCreator<?> creator) {
    getGsonForUpdate(instance, creator).fromJson(jsonAsString, instance.getClass());
  }

  public static FieldNamingPolicy getFieldNamingPolicy() {
    return namingPolicy;
  }
}
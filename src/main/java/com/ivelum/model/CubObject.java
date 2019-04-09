package com.ivelum.model;

import com.google.gson.annotations.SerializedName;
import com.ivelum.exception.InvalidRequestException;
import com.ivelum.net.Params;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * To ignore some fields in serialization you can declare static field
 * {@link com.ivelum.model.User#serializationIgnoreFields}.
 *
 */
public class CubObject {
  protected String apiKey;

  public String id;

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getApiKey() {
    return apiKey;
  }

  public UpdateExistingInstanceCreator<?> getInstanceCreator() {
    return new UpdateExistingInstanceCreator<CubObject>(this);
  }

  /**
   * Fill params object with model serializable fields. Usefull for POST http requests.
   *
   * @param params Params object will be filled using current model.
   * @throws InvalidRequestException Error setting params from object.
   */
  public void toParams(Params params) throws InvalidRequestException {
    assert params != null;
    List<String> toIgnore;

    try {
      Field toIgnoreField = this.getClass().getField("serializationIgnoreFields");
      toIgnore = (List<String>) toIgnoreField.get(this);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      toIgnore = new LinkedList<>();
    }

    for (Field field : this.getClass().getDeclaredFields()) {
      field.setAccessible(true);

      int fieldModifiers = field.getModifiers();
      if (Modifier.isStatic(fieldModifiers) || !Modifier.isPublic(fieldModifiers)) {
        continue;
      }
      if (toIgnore != null && toIgnore.contains(field.getName())) {
        continue;
      }
      Object value;
      try {
        value = field.get(this);
      } catch (IllegalAccessException e) {
        throw new InvalidRequestException("Can not serialize object", e);
      }

      if (value instanceof ExpandableField<?>) {
        params.setValue(nameFromField(field), ((ExpandableField) value).getId());
      } else if (isNotEmptyExpandableList(value)) {
        List<String> values = new LinkedList<>();
        for (Object f: (List<?>) value) {
          values.add(((ExpandableField<?>) f).getId());
        }
        params.setValue(nameFromField(field), values);
      } else {
        params.setValue(nameFromField(field), value);
      }

    }
  }

  protected boolean isNotEmptyExpandableList(Object value) {
    if (!(value instanceof List<?>) || ((List) value).size() == 0) {
      return false;
    }
    return ((List) value).get(0) instanceof ExpandableField<?>;
  }

  protected String nameFromField(Field field) {
    SerializedName annotation = field.getAnnotation(SerializedName.class);
    if (annotation == null) {
      return Factory.getFieldNamingPolicy().translateName(field);
    }

    return annotation.value();
  }
}

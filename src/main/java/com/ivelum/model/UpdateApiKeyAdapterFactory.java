package com.ivelum.model;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class UpdateApiKeyAdapterFactory implements TypeAdapterFactory {
  String apiKey;

  public UpdateApiKeyAdapterFactory(String apiKey) {
    this.apiKey = apiKey;
  }

  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

    return new TypeAdapter<T>() {
      public void write(JsonWriter out, T value) throws IOException {
        delegate.write(out, value);
      }

      public T read(JsonReader in) throws IOException {
        T obj = delegate.read(in);
        if (obj instanceof CubObject) {
          // sets even nullable apiKeys to allow children override behaviour
          ((CubObject) obj).setApiKey(apiKey);
        }
        return obj;
      }
    };
  }
}
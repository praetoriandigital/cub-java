package com.ivelum.model;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;


public class UpdateExistingInstanceCreator<T> implements InstanceCreator<T> {
  T instance;

  public UpdateExistingInstanceCreator(T instance) {
    this.instance = instance;
  }

  @Override
  public T createInstance(Type type) {

    return instance;
  }
}
package com.ivelum.model;

public class ExpandableField<T> {
  private String id;
  private T expandedObject;

  public ExpandableField(String id, T expandedObject) {
    this.id = id;
    this.expandedObject = expandedObject;
  }

  public boolean isExpanded() {
    return expandedObject != null;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public T getExpanded() {
    return expandedObject;
  }

}

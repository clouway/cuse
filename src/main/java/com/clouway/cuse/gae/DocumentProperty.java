package com.clouway.cuse.gae;

import com.google.appengine.api.search.Field;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class DocumentProperty {

  private String name;
  private Field value;

  public DocumentProperty(String name, Field value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public Field getValue() {
    return value;
  }

}

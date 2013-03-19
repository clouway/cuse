package com.clouway.cuse.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class EqualitySearchFilter implements SearchFilter {

  private final String value;

  public EqualitySearchFilter(String value) {
    this.value = value;
  }

  public EqualitySearchFilter(Boolean value) {
    this.value = String.valueOf(value);
  }

  public String getValue() {
    return value;
  }
}

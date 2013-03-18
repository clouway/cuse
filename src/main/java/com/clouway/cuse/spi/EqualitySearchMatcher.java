package com.clouway.cuse.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class EqualitySearchMatcher implements SearchMatcher {

  private final String value;

  public EqualitySearchMatcher(String value) {
    this.value = value;
  }

  public EqualitySearchMatcher(Boolean value) {
    this.value = String.valueOf(value);
  }

  public String getValue() {
    return value;
  }
}

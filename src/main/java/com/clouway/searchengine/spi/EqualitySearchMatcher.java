package com.clouway.searchengine.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class EqualitySearchMatcher implements SearchMatcher {

  private final String value;

  public EqualitySearchMatcher(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}

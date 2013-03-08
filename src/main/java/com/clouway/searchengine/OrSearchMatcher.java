package com.clouway.searchengine;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class OrSearchMatcher implements SearchMatcher {

  private String[] values;

  public OrSearchMatcher(String... values) {
    this.values = values;
  }

  public String getValue() {

    String value = "";

    for (int i = 0; i < values.length; i++) {

      String s = values[i];

      if (i < values.length - 1) {
        value += s + " OR ";
      } else {
        value += s;
      }
    }

    return value;
  }
}

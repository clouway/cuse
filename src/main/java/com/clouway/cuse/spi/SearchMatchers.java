package com.clouway.cuse.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class SearchMatchers {

  public static SearchMatcher is(String value) {
    return new EqualitySearchMatcher(value);
  }

  public static SearchMatcher isAnyOf(String... values) {
    return new OrSearchMatcher(values);
  }
}

package com.clouway.cuse.spi;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class SearchMatchers {

  public static SearchMatcher is(String value) {
    return new EqualitySearchMatcher(value);
  }

  public static SearchMatcher is(Boolean value) {
    return new EqualitySearchMatcher(value);
  }

  public static SearchMatcher isAnyOf(String... values) {
    return new OrSearchMatcher(values);
  }

  public static SearchMatcher isAnyOf(List<Long> values) {
    return new OrSearchMatcher(values);
  }
}

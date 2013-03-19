package com.clouway.cuse.spi;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class SearchFilters {

  public static SearchFilter is(String value) {
    return new EqualitySearchFilter(value);
  }

  public static SearchFilter is(Boolean value) {
    return new EqualitySearchFilter(value);
  }

  public static SearchFilter isAnyOf(String... values) {
    return new OrSearchFilter(values);
  }

  public static SearchFilter isAnyOf(List<Long> values) {
    return new OrSearchFilter(values);
  }
}

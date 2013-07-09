package com.clouway.cuse.gae.filters;

import com.clouway.cuse.spi.EmptySearchFilterException;
import com.clouway.cuse.spi.filters.SearchFilter;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class SearchFilters {

  public static SearchFilter is(String value) {
    if (value == null || "".equals(value.trim())) {
      throw new EmptySearchFilterException();
    }
    return new EqualitySearchFilter(value);
  }

  public static SearchFilter is(Boolean value) {
    return new EqualitySearchFilter(String.valueOf(value));
  }

  public static SearchFilter is(Integer value) {
    return new EqualitySearchFilter(String.valueOf(value));
  }

  public static SearchFilter is(Long value) {
    return new EqualitySearchFilter(String.valueOf(value));
  }

  public static SearchFilter isAnyOf(String... values) {
    return new OrSearchFilter(values);
  }

  public static SearchFilter isAnyOf(List<Long> values) {
    if (values == null || values.size() == 0) {
      throw new EmptySearchFilterException();
    }
    return new OrSearchFilter(values);
  }

  public static SearchFilter anyIs(String value) {
    return new MultiFieldValueFilter(value);
  }
}

package com.clouway.searchengine.spi;

import com.clouway.searchengine.spi.EqualitySearchMatcher;
import com.clouway.searchengine.spi.OrSearchMatcher;
import com.clouway.searchengine.spi.SearchMatcher;

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

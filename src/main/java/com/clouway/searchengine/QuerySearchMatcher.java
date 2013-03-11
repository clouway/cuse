package com.clouway.searchengine;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class QuerySearchMatcher implements SearchMatcher {

  private final String query;

  public QuerySearchMatcher(String query) {
    this.query = query;
  }

  @Override
  public String getValue() {
    return query;
  }
}

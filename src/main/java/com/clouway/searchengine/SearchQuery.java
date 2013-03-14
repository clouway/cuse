package com.clouway.searchengine;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class SearchQuery {

  private String query;

  public SearchQuery(String query) {
    this.query = query;
  }

  public String getValue() {
    return query;
  }
}

package com.clouway.cuse.gae.filters;

import com.clouway.cuse.spi.filters.SearchFilter;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GreaterThanOrEqualToFilter implements SearchFilter {

  private final SearchDate searchDate;

  public GreaterThanOrEqualToFilter(SearchDate searchDate) {
    this.searchDate = searchDate;
  }

  @Override
  public String getValue(List<String> fields) {
    return fields.get(0) + " >= " + searchDate.getValue();
  }
}

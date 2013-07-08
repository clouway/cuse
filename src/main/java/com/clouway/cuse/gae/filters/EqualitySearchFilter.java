package com.clouway.cuse.gae.filters;

import com.clouway.cuse.spi.filters.SearchFilter;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class EqualitySearchFilter implements SearchFilter {

  private final String value;

  public EqualitySearchFilter(String value) {
    this.value = value;
  }

  public String getValue(List<String> fields) {
    return fields.get(0) + ":" + value;
  }
}

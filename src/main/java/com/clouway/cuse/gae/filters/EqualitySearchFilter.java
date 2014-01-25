package com.clouway.cuse.gae.filters;

import com.clouway.cuse.spi.filters.SearchFilter;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class EqualitySearchFilter implements SearchFilter {

  private String value;
  private List<String> values;

  public EqualitySearchFilter(String value) {
    this.value = value;
  }

  public EqualitySearchFilter(SearchableDate searchableDate) {
    this.value = searchableDate.getValue();
  }

  public EqualitySearchFilter(List<String> values) {
    this.values = values;
  }

  public String getValue(List<String> fields) {

    String filter = "";

    if (value != null && !"".equals(value)) {
      filter = fields.get(0) + ":" + value + " ";
    }

    if (values != null && !values.isEmpty()) {
      for (String value : values) {
        filter = filter.concat(fields.get(0) + ":" + value + " ");
      }
    }

    return filter;
  }
}

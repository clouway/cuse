package com.clouway.cuse.gae.filters;

import com.clouway.cuse.spi.filters.SearchFilter;

import java.util.List;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class MultiFieldValueFilter implements SearchFilter {


  private String value;

  public MultiFieldValueFilter(String value) {
    this.value = value;
  }

  public String getValue(List<String> fields) {

    String filter = "";

    for (int i = 0; i < fields.size(); i++) {

      String filed = fields.get(i);

      if (i < fields.size() - 1) {
        filter += filed + ":" + value + " OR ";
      } else {
        filter += filed + ":" + value;
      }
    }

    return filter;
  }
}


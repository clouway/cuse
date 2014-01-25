package com.clouway.cuse.gae.filters;

import com.clouway.cuse.spi.filters.SearchFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class OrSearchFilter implements SearchFilter {

  private List<String> listOfValues = new ArrayList<String>();

  public OrSearchFilter(String... values) {
    Collections.addAll(listOfValues, values);
  }

  public <T> OrSearchFilter(List<T> values) {

    for (T value : values) {
      listOfValues.add(String.valueOf(value));
    }
  }

  public String getValue(List<String> fields) {
    StringBuilder builder = new StringBuilder();

    if (listOfValues.size() > 0) {
      builder.append("(");
    }

    for (int i = 0; i < listOfValues.size(); i++) {

      String value = listOfValues.get(i);

      if (i < listOfValues.size() - 1) {
        builder.append(value).append(" OR ");
      } else {
        builder.append(value).append(")");
      }
    }

    return fields.get(0) + ":" + builder.toString();
  }
}

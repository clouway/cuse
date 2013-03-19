package com.clouway.cuse.spi;

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

  public OrSearchFilter(List<Long> values) {

    for (Long value : values) {
      listOfValues.add(String.valueOf(value));
    }
  }

  public String getValue() {

    String value = "";

    for (int i = 0; i < listOfValues.size(); i++) {

      String s = listOfValues.get(i);

      if (i < listOfValues.size() - 1) {
        value += s + " OR ";
      } else {
        value += s;
      }
    }

    return value;
  }
}

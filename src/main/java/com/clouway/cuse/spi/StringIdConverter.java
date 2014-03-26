package com.clouway.cuse.spi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class StringIdConverter implements IdConverter<String> {

  @Override
  public List<String> convert(List<String> values) {

    List<String> result = new ArrayList<String>();

    for (String value : values) {
      result.add(value);
    }

    return result;
  }

  @Override
  public String convertFrom(Object id) {
    if(id != null && id instanceof String) {
      return (String) id;
    }
    return null;
  }
}

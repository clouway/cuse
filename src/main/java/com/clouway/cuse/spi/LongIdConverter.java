package com.clouway.cuse.spi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class LongIdConverter implements IdConverter<Long> {

  @Override
  public List<Long> convert(List<String> values) {

    List<Long> result = new ArrayList<Long>();

    for (String value : values) {
      result.add(Long.valueOf(value));
    }

    return result;
  }

  @Override
  public String convertFrom(Object id) {
    if(id != null && id instanceof Long) {
      return Long.toString((Long) id);
    }
    return null;
  }
}

package com.clouway.searchengine.spi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class LongIdConvertor implements IdConvertor<Long> {

  @Override
  public List<Long> convert(List<String> values) {

    List<Long> result = new ArrayList<Long>();

    for (String value : values) {
      result.add(Long.valueOf(value));
    }

    return result;
  }
}

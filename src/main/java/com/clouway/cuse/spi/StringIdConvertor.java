package com.clouway.cuse.spi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class StringIdConvertor implements IdConvertor<String> {

  @Override
  public List<String> convert(List<String> values) {

    List<String> result = new ArrayList<String>();

    for (String value : values) {
      result.add(value);
    }

    return result;
  }
}

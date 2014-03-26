package com.clouway.cuse.spi;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IdConverter<T> {

  List<T> convert(List<String> values);

  String convertFrom(Object id);
}

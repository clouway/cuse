package com.clouway.searchengine.spi;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IdConvertor<T> {

  List<T> convert(List<String> values);
}

package com.clouway.searchengine;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IdConvertor<T> {

  List<T> convert(List<String> values);
}

package com.clouway.searchengine;

import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IndexingStrategy<T> {

  String getIndexName();

  List<String> getFields();

  String getId(T t);
}

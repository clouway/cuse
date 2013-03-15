package com.clouway.cuse.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IndexingStrategy<T> {

  String getIndexName();

  String getId(T t);

  IndexingSchema getIndexingSchema();
}

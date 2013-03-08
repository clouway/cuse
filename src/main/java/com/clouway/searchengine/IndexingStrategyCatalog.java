package com.clouway.searchengine;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IndexingStrategyCatalog {

  IndexingStrategy get(Class aClass);
}

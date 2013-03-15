package com.clouway.cuse.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public interface IndexingStrategyCatalog {

  IndexingStrategy get(Class<?> indexClazz);
}

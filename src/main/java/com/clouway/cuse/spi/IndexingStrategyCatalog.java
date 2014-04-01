package com.clouway.cuse.spi;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
interface IndexingStrategyCatalog {

  IndexingStrategy get(Class<?> indexClazz);
}

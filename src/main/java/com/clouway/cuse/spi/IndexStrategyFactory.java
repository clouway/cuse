package com.clouway.cuse.spi;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
interface IndexStrategyFactory {

  IndexingStrategy create(Class<?> indexClazz);

}

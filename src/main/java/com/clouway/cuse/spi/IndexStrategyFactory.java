package com.clouway.cuse.spi;

import com.google.inject.ImplementedBy;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
@ImplementedBy(IndexStrategyFactoryImpl.class)
public interface IndexStrategyFactory {

  IndexingStrategy create(Class<?> indexClazz);

}

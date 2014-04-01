package com.clouway.cuse.spi;

import com.clouway.cuse.gae.GaeSearchApiIndexRegister;
import com.clouway.cuse.gae.GaeSearchApiMatchedIdObjectFinder;
import com.google.inject.util.Providers;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class SearchEngineFactory {

  public static SearchEngine create(EntityLoader entityLoader) {
    return create(entityLoader, new DefaultIdConverterCatalog());
  }

  public static SearchEngine create(EntityLoader entityLoader, IdConverterCatalog idConverterCatalog) {
    IndexingStrategyCatalogImpl indexingStrategyCatalog = new IndexingStrategyCatalogImpl(new IndexStrategyFactoryImpl(
            Providers.of(idConverterCatalog),
            Providers.<IndexSchemaFillActionsCatalog>of(new InMemoryIndexSchemaFillActionsCatalog())));

    GaeSearchApiIndexRegister indexRegister = new GaeSearchApiIndexRegister();
    GaeSearchApiMatchedIdObjectFinder objectIdFinder = new GaeSearchApiMatchedIdObjectFinder();

    return new SearchEngineImpl(entityLoader, idConverterCatalog, indexingStrategyCatalog, indexRegister, objectIdFinder);
  }
}

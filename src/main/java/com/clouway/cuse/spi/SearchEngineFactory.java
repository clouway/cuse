package com.clouway.cuse.spi;

import com.clouway.cuse.gae.GaeSearchApiIndexRegister;
import com.clouway.cuse.gae.GaeSearchApiMatchedIdObjectFinder;
import com.google.inject.util.Providers;

/**
 * This class is used for creation of instance on {@link com.clouway.cuse.spi.SearchEngine}
 *
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class SearchEngineFactory {

  /**
   * Create SearchEngine with given EntityLoader
   *
   * @param entityLoader which will be used from SearchEngine
   * @return SearchEngine
   */
  public static SearchEngine create(EntityLoader entityLoader) {
    return create(entityLoader, new DefaultIdConverterCatalog());
  }

  /**
   * Create SearchEngine with given EntityLoader and IdConverterCatalog
   *
   * @param entityLoader       which will be used from SearchEngine
   * @param idConverterCatalog which will be used from SearchEngine
   * @return SearchEngines
   */
  public static SearchEngine create(EntityLoader entityLoader, IdConverterCatalog idConverterCatalog) {
    IndexingStrategyCatalogImpl indexingStrategyCatalog = new IndexingStrategyCatalogImpl(new AnnotatedIndexStrategyFactory(
            Providers.of(idConverterCatalog),
            Providers.<IndexSchemaFillActionsCatalog>of(new InMemoryIndexSchemaFillActionsCatalog())));

    GaeSearchApiIndexRegister indexRegister = new GaeSearchApiIndexRegister();
    GaeSearchApiMatchedIdObjectFinder objectIdFinder = new GaeSearchApiMatchedIdObjectFinder();

    return new SearchEngineImpl(entityLoader, idConverterCatalog, indexingStrategyCatalog, indexRegister, objectIdFinder);
  }
}

package com.clouway.cuse.spi;

import com.google.inject.util.Providers;

/**
 * Used is test where should be used together index strategy configuration and annotated index configuration
 *
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class TestingStrategyCatalog extends IndexingStrategyCatalogImpl {

  public TestingStrategyCatalog() {
    super(new IndexStrategyFactoryImpl(
                    Providers.<IdConverterCatalog>of(new DefaultIdConverterCatalog()),
                    Providers.<IndexSchemaFillActionsCatalog>of(new InMemoryIndexSchemaFillActionsCatalog()))
    );
  }

  public TestingStrategyCatalog(IdConverterCatalog idConverterCatalog) {
    super(new IndexStrategyFactoryImpl(
                    Providers.of(idConverterCatalog),
                    Providers.<IndexSchemaFillActionsCatalog>of(new InMemoryIndexSchemaFillActionsCatalog()))
    );
  }
}

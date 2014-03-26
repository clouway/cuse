package com.clouway.cuse.gae;

import com.clouway.cuse.spi.*;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;

import java.util.Set;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class SearchApiCuseBindingModule extends AbstractModule {

  private final Class<? extends EntityLoader> entityLoaderClazz;

  private IndexingStrategyCatalogImpl indexingStrategyCatalog;


  protected SearchApiCuseBindingModule(Class<? extends EntityLoader> entityLoaderClazz) {
    this.entityLoaderClazz = entityLoaderClazz;
  }

  @Override
  protected void configure() {
    bind(SearchEngine.class).to(SearchEngineImpl.class);
    bind(IdConverterCatalog.class).to(DefaultIdConverterCatalog.class);
    bind(IndexRegister.class).to(GaeSearchApiIndexRegister.class);
    bind(MatchedIdObjectFinder.class).to(GaeSearchApiMatchedIdObjectFinder.class);
  }

  @Provides
  IndexingStrategyCatalog getIndexingStrategyCatalog(IndexStrategyFactory indexStrategyFactory, Injector injector, Set<GaeSearchApiCuseModule.IndexStrategyMetadata> indexStrategyMetadatas){

    if(indexingStrategyCatalog == null) {
      indexingStrategyCatalog = new IndexingStrategyCatalogImpl(indexStrategyFactory);
    }

    for (GaeSearchApiCuseModule.IndexStrategyMetadata indexStrategyMetadata : indexStrategyMetadatas) {
      IndexingStrategy<?> indexingStrategy = injector.getInstance(indexStrategyMetadata.getStrategyClazz());
      indexingStrategyCatalog.put(indexStrategyMetadata.getIndexClazz(), indexingStrategy);
    }

    return indexingStrategyCatalog;
  }

  @Provides
  EntityLoader getEntityLoader(Injector injector) {
    return injector.getInstance(entityLoaderClazz);
  }

  @Override
  public boolean equals(Object o) {
    // Is only ever installed internally, so we don't need to check state.
    return o instanceof SearchApiCuseBindingModule;
  }

  @Override
  public int hashCode() {
    return SearchApiCuseBindingModule.class.hashCode();
  }


}
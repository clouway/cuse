package com.clouway.cuse.gae;

import com.clouway.cuse.spi.EntityLoader;
import com.clouway.cuse.spi.SearchEngine;
import com.clouway.cuse.spi.SearchEngineFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class SearchApiCuseBindingModule extends AbstractModule {

  private final Class<? extends EntityLoader> entityLoaderClazz;

  protected SearchApiCuseBindingModule(Class<? extends EntityLoader> entityLoaderClazz) {
    this.entityLoaderClazz = entityLoaderClazz;
  }

  @Override
  protected void configure() {
  }

  @Provides
  @Singleton
  public SearchEngine getSearchEngine(EntityLoader entityLoader) {
    return SearchEngineFactory.create(entityLoader);
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
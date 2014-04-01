package com.clouway.cuse.gae;

import com.clouway.cuse.spi.EntityLoader;
import com.google.inject.AbstractModule;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeSearchApiCuseModule extends AbstractModule {

  private final Class<? extends EntityLoader> entityLoaderClazz;

  @Override
  protected final void configure() {
    install(new SearchApiCuseBindingModule(entityLoaderClazz));
  }

  protected GaeSearchApiCuseModule(Class<? extends EntityLoader> entityLoaderClazz) {
    this.entityLoaderClazz = entityLoaderClazz;
  }
}

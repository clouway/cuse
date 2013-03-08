package com.clouway.searchengine;

import org.junit.Before;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class InMemoryEntityLoaderTest extends EntityLoaderContractTest {

  InMemoryEntityLoader entityLoader;

  @Before
  public void setUp() {
    entityLoader = new InMemoryEntityLoader();
  }

  @Override
  public EntityLoader createEntityLoader() {
    return entityLoader;
  }

  @Override
  public void store(Long id, Object object) {
    entityLoader.store(id, object);
  }
}

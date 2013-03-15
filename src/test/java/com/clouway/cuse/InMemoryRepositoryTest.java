package com.clouway.cuse;

import com.clouway.cuse.spi.EntityLoader;
import org.junit.Before;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class InMemoryRepositoryTest extends EntityLoaderContractTest {

  InMemoryRepository repository;

  @Before
  public void setUp() {
    repository = new InMemoryRepository();
  }

  @Override
  public EntityLoader createEntityLoader() {
    return repository;
  }

  @Override
  public void store(Long id, Object object) {
    repository.store(id, object);
  }
}

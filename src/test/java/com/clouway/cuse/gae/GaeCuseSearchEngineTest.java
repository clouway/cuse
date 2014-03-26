package com.clouway.cuse.gae;

import com.clouway.cuse.InMemoryRepository;
import com.clouway.cuse.SearchEngineContractTest;
import com.clouway.cuse.spi.EntityLoader;
import com.clouway.cuse.spi.SearchEngine;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Before;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeCuseSearchEngineTest extends SearchEngineContractTest {

  @Inject
  private SearchEngine searchEngine;

  private InMemoryRepository inMemoryRepository = new InMemoryRepository();


  @Override
  @Before
  public void setUp() {
    Injector injector = Guice.createInjector(
            // configure the provider to return always the same in memory implementation of the strategy catalog
            new SearchApiCuseBindingModule(InMemoryRepository.class) {

              @Override
              EntityLoader getEntityLoader(Injector injector) {
                return inMemoryRepository;
              }
            }
    );
    injector.injectMembers(this);
    super.setUp();
  }

  @Override
  protected SearchEngine createSearchEngine() {
    return searchEngine;
  }

  @Override
  protected InMemoryRepository createInMemoryRepository() {
    return inMemoryRepository;
  }

}

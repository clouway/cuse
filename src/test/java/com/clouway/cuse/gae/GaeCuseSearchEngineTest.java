package com.clouway.cuse.gae;

import com.clouway.cuse.Employee;
import com.clouway.cuse.EmployeeIndexingStrategy;
import com.clouway.cuse.InMemoryRepository;
import com.clouway.cuse.SearchEngineContractTest;
import com.clouway.cuse.User;
import com.clouway.cuse.UserIndexingStrategy;
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
            new SearchApiCuseBindingModule(InMemoryRepository.class){

      @Override
      EntityLoader getEntityLoader(Injector injector) {
        return inMemoryRepository;
      }
    },
//        installing the module many times because of binding index classes to their index strategies
//        in different modules - independent binding in each guice module - multibinding
            new GaeSearchApiCuseModule(InMemoryRepository.class) {

                                               @Override
                                               protected void configureIndexStrategies() {
                                                 objectIndex(User.class).through(UserIndexingStrategy.class);
                                               }
                                             },
            new GaeSearchApiCuseModule(InMemoryRepository.class) {

                                               @Override
                                               protected void configureIndexStrategies() {
                                                 objectIndex(Employee.class).through(EmployeeIndexingStrategy.class);
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

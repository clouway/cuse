package com.clouway.searchengine;

import com.google.appengine.tools.development.testing.LocalSearchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class SearchEngineApiTest {

  class Dog {

    public final Long id;
    public final String name;

    public Dog(Long id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  class DogIndexingStrategy implements IndexingStrategy<Dog> {

    public String getIndexName() {
      return Dog.class.getSimpleName();
    }

    @Override
    public List<String> getFields() {
      return Arrays.asList("name");
    }

    @Override
    public String getId(Dog dog) {
      return dog.id.toString();
    }
  }

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalSearchServiceTestConfig());
  private InMemoryEntityLoader entityLoader;
  private SearchEngine searchEngine;

  @Before
  public void setUp() {
    helper.setUp();
    entityLoader = new InMemoryEntityLoader();

    searchEngine = new SearchEngineImpl(entityLoader, new IndexingStrategyCatalog() {
      @Override
      public IndexingStrategy get(Class aClass) {
        return new DogIndexingStrategy();
      }
    });
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void test() {

    Dog dog = new Dog(1l, "Jack");

    entityLoader.store(dog.id, dog);
    searchEngine.register(dog);

    List<Dog> dogs = searchEngine.search(Dog.class).where("name", SearchMatchers.is("Jack")).returnAll().now();

    assertThat(dogs.size(), is(equalTo(1)));
    assertThat(dogs.get(0).name, is(equalTo("Jack")));
  }
}
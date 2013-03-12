package com.clouway.searchengine;

import com.google.appengine.tools.development.testing.LocalSearchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class SearchEngineApiTest {

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

        if (aClass.equals(Employee.class)) {
          return new EmployeeIndexingStrategy();
        }
        return new UserIndexingStrategy();
      }
    });
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void searchByMatchingFieldValue() {

    store(new User(1l, "Jack"));

    List<User> dogs = searchEngine.search(User.class).where("name", SearchMatchers.is("Jack")).returnAll().now();

    assertThat(dogs.size(), is(equalTo(1)));
    assertThat(dogs.get(0).name, is(equalTo("Jack")));
  }

  @Test
  public void searchByNotMatchingFieldValue() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("name", SearchMatchers.is("Jim")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test(expected = EmptyMatcherException.class)
  public void searchByEmptyFieldValue() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("name", SearchMatchers.is("")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test(expected = EmptyMatcherException.class)
  public void searchByFieldValueContainingOnlyWhiteSpaces() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("name", SearchMatchers.is("   ")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByNotExistingFieldValue() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("age", SearchMatchers.is("12")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByMatchingAnyOfTheGivenValues() {

    store(new User(1l, "Jack"), new User(2l, "Jim"));

    List<User> result = searchEngine.search(User.class).where("name", SearchMatchers.isAnyOf("Jack", "Jim")).returnAll().now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(1).id, is(2l));
  }

  @Test
  public void searchByMatchingOneValueThatMatchesAny() {

    store(new User(1l, "Jack"), new User(2l, "Jim"));

    List<User> result = searchEngine.search(User.class).where("name", SearchMatchers.isAnyOf("Jim")).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(2l));
  }

  @Test
  public void searchByMatchingAnyValuesForNotExistingField() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("age", SearchMatchers.isAnyOf("12", "14")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByQueryComposedOfTwoWords() {

    store(new User(1l, "Jack Smith"));

    List<User> result = searchEngine.search(User.class).where(SearchMatchers.query("Jack Smith")).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchByQueryComposedOfOneWord() {

    store(new User(1l, "Jack Smith"));

    List<User> result = searchEngine.search(User.class).where(SearchMatchers.query("Jack")).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchForManyMatchingTheGivenQuery() {

    store(new User(1l, "Jack Smith"), new User(2l, "Johny Smith"));

    List<User> result = searchEngine.search(User.class).where(SearchMatchers.query("Smith")).returnAll().now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(1).id, is(2l));
  }

  @Test(expected = InvalidSearchException.class)
  public void searchByEmptyQuery() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where(SearchMatchers.query("")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchInGivenIndex() {

    entityLoader.store(1l, new User(1l));
    searchEngine.register(new Employee(1l, "John"));

    List<User> result = searchEngine.search(User.class).inIndex(Employee.class)
                                                       .where("firstName", SearchMatchers.is("John"))
                                                       .returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchByMatchingTwoFieldValues() {

    store(new Employee(1l, "John", "Adams"));

    List<Employee> result = searchEngine.search(Employee.class).where("firstName", SearchMatchers.is("John"))
                                                               .where("lastName", SearchMatchers.is("Adams"))
                                                               .returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).firstName, is("John"));
    assertThat(result.get(0).lastName, is("Adams"));
  }

  @Test
  public void searchByLimitingSearchResult() {

    store(new Employee(1l, "Jack Smith"), new Employee(2l, "Jack Samuel"), new Employee(3l, "Jack Jameson"));

    List<Employee> result = searchEngine.search(Employee.class).where("firstName", SearchMatchers.is("Jack"))
                                                               .returnAll()
                                                               .limit(2)
                                                               .now();

    assertThat(result.size(), is(2));
  }

  @Test(expected = InvalidSearchException.class)
  public void searchWithoutSpecifyingMatcherAndQuery() {

    store(new User(1l, "John"));

    List<User> result = searchEngine.search(User.class).returnAll().now();

    assertThat(result.size(), is(0));
  }

  private void store(User... users) {

    for (User user : users) {
      entityLoader.store(user.id, user);
      searchEngine.register(user);
    }
  }

  private void store(Employee... employees) {

    for (Employee employee : employees) {
      entityLoader.store(employee.id, employee);
      searchEngine.register(employee);
    }
  }
}
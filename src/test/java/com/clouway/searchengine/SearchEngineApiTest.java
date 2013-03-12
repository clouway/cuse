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

  class UserIndexingStrategy implements IndexingStrategy<User> {

    public String getIndexName() {
      return User.class.getSimpleName();
    }

    @Override
    public List<String> getFields() {
      return Arrays.asList("name");
    }

    @Override
    public String getId(User user) {
      return user.id.toString();
    }
  }

  class EmployeeIndexingStrategy implements IndexingStrategy<Employee> {

    @Override
    public String getIndexName() {
      return Employee.class.getSimpleName();
    }

    @Override
    public List<String> getFields() {
      return Arrays.asList("firstName", "lastName");
    }

    @Override
    public String getId(Employee employee) {
      return String.valueOf(employee.id);
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

    User dog = new User(1l, "Jack");

    entityLoader.store(dog.id, dog);
    searchEngine.register(dog);

    List<User> dogs = searchEngine.search(User.class).where("name", SearchMatchers.is("Jack")).returnAll().now();

    assertThat(dogs.size(), is(equalTo(1)));
    assertThat(dogs.get(0).name, is(equalTo("Jack")));
  }

  @Test
  public void searchByNotMatchingFieldValue() {

    User dog = new User(1l, "Jack");

    entityLoader.store(dog.id, dog);
    searchEngine.register(dog);

    List<User> result = searchEngine.search(User.class).where("name", SearchMatchers.is("Jim")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test(expected = EmptyMatcherException.class)
  public void searchByEmptyFieldValue() {

    User dog = new User(1l, "Jack");

    entityLoader.store(dog.id, dog);
    searchEngine.register(dog);

    List<User> result = searchEngine.search(User.class).where("name", SearchMatchers.is("")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test(expected = EmptyMatcherException.class)
  public void searchByFieldValueContainingOnlyWhiteSpaces() {

    User dog = new User(1l, "Jack");

    entityLoader.store(dog.id, dog);
    searchEngine.register(dog);

    List<User> result = searchEngine.search(User.class).where("name", SearchMatchers.is("   ")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByNotExistingFieldValue() {

    User dog = new User(1l, "Jack");

    entityLoader.store(dog.id, dog);
    searchEngine.register(dog);

    List<User> result = searchEngine.search(User.class).where("age", SearchMatchers.is("12")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByMatchingAnyOfTheGivenValues() {

    User dog = new User(1l, "Jack");
    User anotherDog = new User(2l, "Jim");

    entityLoader.store(dog.id, dog);
    entityLoader.store(anotherDog.id, anotherDog);
    searchEngine.register(dog);
    searchEngine.register(anotherDog);

    List<User> result = searchEngine.search(User.class).where("name", SearchMatchers.isAnyOf("Jack", "Jim")).returnAll().now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(1).id, is(2l));
  }

  @Test
  public void searchByMatchingOneValueThatMatchesAny() {

    User dog = new User(1l, "Jack");
    User anotherDog = new User(2l, "Jim");

    entityLoader.store(dog.id, dog);
    entityLoader.store(anotherDog.id, anotherDog);
    searchEngine.register(dog);
    searchEngine.register(anotherDog);

    List<User> result = searchEngine.search(User.class).where("name", SearchMatchers.isAnyOf("Jim")).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(2l));
  }

  @Test
  public void searchByMatchingAnyValuesForNotExistingField() {

    User dog = new User(1l, "Jack");
    User anotherDog = new User(2l, "Jim");

    entityLoader.store(dog.id, dog);
    entityLoader.store(anotherDog.id, anotherDog);
    searchEngine.register(dog);
    searchEngine.register(anotherDog);

    List<User> result = searchEngine.search(User.class).where("age", SearchMatchers.isAnyOf("12", "14")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByQueryComposedOfTwoWords() {

    User dog = new User(1l, "Jack Smith");

    entityLoader.store(dog.id, dog);
    searchEngine.register(dog);

    List<User> result = searchEngine.search(User.class).where(SearchMatchers.query("Jack Smith")).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchByQueryComposedOfOneWord() {

    User dog = new User(1l, "Jack Smith");

    entityLoader.store(dog.id, dog);
    searchEngine.register(dog);

    List<User> result = searchEngine.search(User.class).where(SearchMatchers.query("Jack")).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchForManyMatchingTheGivenQuery() {

    User dog = new User(1l, "Jack Smith");
    User anotherDog = new User(2l, "Johny Smith");

    entityLoader.store(dog.id, dog);
    entityLoader.store(anotherDog.id, anotherDog);
    searchEngine.register(dog);
    searchEngine.register(anotherDog);

    List<User> result = searchEngine.search(User.class).where(SearchMatchers.query("Smith")).returnAll().now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(1).id, is(2l));
  }

  @Test(expected = InvalidSearchException.class)
  public void searchByEmptyQuery() {

    User dog = new User(1l, "Jack");

    entityLoader.store(dog.id, dog);
    searchEngine.register(dog);

    List<User> result = searchEngine.search(User.class).where(SearchMatchers.query("")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchInGivenIndex() {

    User user = new User(1l);
    Employee employee = new Employee(1l, "John");

    entityLoader.store(user.id, user);
    searchEngine.register(employee);

    List<User> result = searchEngine.search(User.class).inIndex(Employee.class)
                                                       .where("firstName", SearchMatchers.is("John"))
                                                       .returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchByMatchingTwoFieldValues() {

    Employee employee = new Employee(1l, "John", "Adams");

    entityLoader.store(1l, employee);
    searchEngine.register(employee);

    List<Employee> result = searchEngine.search(Employee.class).where("firstName", SearchMatchers.is("John"))
                                                               .where("lastName", SearchMatchers.is("Adams"))
                                                               .returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).firstName, is("John"));
    assertThat(result.get(0).lastName, is("Adams"));
  }

  @Test
  public void searchByLimitingSearchResult() {

    Employee employee = new Employee(1l, "Jack Smith");
    Employee employee2 = new Employee(2l, "Jack Samuel");
    Employee employee3 = new Employee(3l, "Jack Jameson");

    entityLoader.store(employee.id, employee);
    entityLoader.store(employee2.id, employee2);
    entityLoader.store(employee3.id, employee3);

    searchEngine.register(employee);
    searchEngine.register(employee2);
    searchEngine.register(employee3);

    List<Employee> result = searchEngine.search(Employee.class).where("firstName", SearchMatchers.is("Jack"))
                                                               .returnAll()
                                                               .limit(2)
                                                               .now();

    assertThat(result.size(), is(2));
  }

  @Test(expected = InvalidSearchException.class)
  public void searchWithoutSpecifyingMatcherAndQuery() {

    User dog = new User(1l, "John");

    entityLoader.store(dog.id, dog);
    searchEngine.register(dog);

    List<User> result = searchEngine.search(User.class).returnAll().now();

    assertThat(result.size(), is(0));
  }

  class User {

    public Long id;
    public String name;

    public User(Long id) {
      this.id = id;
    }

    public User(Long id, String name) {
      this.id = id;
      this.name = name;
    }
  }

  class Employee {

    public Long id;
    public String firstName;
    public String lastName;

    public Employee(Long id, String firstName) {
      this.id = id;
      this.firstName = firstName;
    }

    public Employee(Long id, String firstName, String lastName) {
      this.id = id;
      this.firstName = firstName;
      this.lastName = lastName;
    }
  }
}
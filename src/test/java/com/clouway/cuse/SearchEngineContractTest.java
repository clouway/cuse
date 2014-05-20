package com.clouway.cuse;

import com.clouway.cuse.gae.filters.SearchFilters;
import com.clouway.cuse.spi.*;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.appengine.tools.development.testing.LocalSearchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.clouway.cuse.Employee.aNewEmployee;
import static com.clouway.cuse.gae.filters.SearchFilters.isAnyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public abstract class SearchEngineContractTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalSearchServiceTestConfig());

  private SearchEngine searchEngine;

  private InMemoryRepository repository;

  private EntityLoader entityLoader = context.mock(EntityLoader.class);

  protected abstract SearchEngine createSearchEngine();

  protected abstract InMemoryRepository createInMemoryRepository();

  @Before
  public void setUp() {

    helper.setUp();

    repository = createInMemoryRepository();
    searchEngine = createSearchEngine();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void searchMatchingFieldValue() {

    store(new User(1l, "Jack"));

    List<User> dogs = searchEngine.search(User.class).where("name", SearchFilters.is("Jack")).returnAll().now();

    assertThat(dogs.size(), is(equalTo(1)));
    assertThat(dogs.get(0).name, is(equalTo("Jack")));
  }

  @Test
  public void noMatchingFieldValue() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("name", SearchFilters.is("Jim")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test(expected = EmptySearchFilterException.class)
  public void searchByEmptyFieldValue() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("name", SearchFilters.is("")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test(expected = EmptySearchFilterException.class)
  public void searchByFieldValueContainingOnlyWhiteSpaces() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("name", SearchFilters.is("   ")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByNotExistingFieldValue() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("age", SearchFilters.is("12")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void matchingAnyOfTheGivenValues() {

    store(new User(1l, "Jack"), new User(2l, "Jim"));

    List<User> result = searchEngine.search(User.class).where("name", isAnyOf("Jack", "Jim")).returnAll().now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(1).id, is(2l));
  }

  @Test
  public void searchByMatchingOneValueThatMatchesAny() {

    store(new User(1l, "Jack"), new User(2l, "Jim"));

    List<User> result = searchEngine.search(User.class).where("name", isAnyOf("Jim")).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(2l));
  }

  @Test
  public void matchingValuesForNonExistingField() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("age", isAnyOf("12", "14")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchForOneMatchByGivenQuery() {

    store(new User(1l, "Jack Smith"));

    List<User> result = searchEngine.search(User.class).where("Jack Smith").returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchForManyMatchesByGivenQuery() {

    store(new User(1l, "Jack Smith"), new User(2l, "Johny Smith"));

    List<User> result = searchEngine.search(User.class).where("Smith").returnAll().now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(1).id, is(2l));
  }

  @Test
  public void searchByEmptyQuery() {

    store(new User(1l, "Jack"));

    List<User> result = searchEngine.search(User.class).where("").returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(0).name, is("Jack"));
  }

  @Test
  public void searchInGivenIndex() {

    repository.store(1l, new User(1l));
    searchEngine.register(new Employee(1l, "John"));

    List<User> result = searchEngine.search(User.class).inIndex(Employee.class)
                                                       .where("firstName", SearchFilters.is("John"))
                                                       .returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchByMatchingTwoFieldValues() {

    store(new Employee(1l, "John", "Adams"));

    List<Employee> result = searchEngine.search(Employee.class).where("firstName", SearchFilters.is("John"))
                                                               .where("lastName", SearchFilters.is("Adams"))
                                                               .returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).firstName, is("John"));
    assertThat(result.get(0).lastName, is("Adams"));
  }

  @Test
  public void limitSearchByPositiveValue() {

    store(new Employee(1l, "Jack Smith"), new Employee(2l, "Jack Samuel"), new Employee(3l, "Jack Jameson"));

    List<Employee> result = searchEngine.search(Employee.class).where("firstName", SearchFilters.is("Jack"))
                                                               .fetchMaximum(2)
                                                               .now();

    sortEmployeesById(result);

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(1).id, is(2l));
  }

  @Test
  public void limitingSearchResultByZero() {

    store(new User(1l, "Jack"), new User(2l, "Jack Adams"));

    List<User> result = searchEngine.search(User.class).where("name", SearchFilters.is("Jack")).fetchMaximum(0).now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(1).id, is(2l));
  }

  @Test(expected = SearchLimitExceededException.class)
  public void exceededSearchLimit() {

    store(new User(1l, "John"));

    List<User> result = searchEngine.search(User.class).where("name", SearchFilters.is("John"))
                                                       .fetchMaximum(1001)
                                                       .now();

    assertThat(result.size(), is(0));
  }

  @Test(expected = NegativeSearchLimitException.class)
  public void negativeSearchLimit() {

    store(new User(1l, "John"));

    List<User> result = searchEngine.search(User.class).where("name", SearchFilters.is("John")).fetchMaximum(-1).now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchWithoutSpecifyingFiltersMatcher() {

    store(new User(1l, "John"));

    List<User> result = searchEngine.search(User.class).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(0).name, is("John"));
  }

  @Test
  public void noMatchingResultsFromFullTextSearch() {

    store(new User(1l, "John Adams"));

    List<User> result = searchEngine.search(User.class).where("Jack").returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void matchingResultsFromFullTextSearch() {

    store(new User(1l, "John Adams"), new User(2l, "John Parker"));

    List<User> result = searchEngine.search(User.class).where("Jo").returnAll().now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).name, is("John Adams"));
    assertThat(result.get(1).name, is("John Parker"));
  }

  @Test
  public void searchReturnsObjectIdsAsString() {

    context.checking(new Expectations() {{
      never(entityLoader);
    }});

    store(new User(1l, "John Adams"), new User(2l, "John Parker"));

    List<String> result = searchEngine.searchIds(String.class).inIndex(User.class)
                                                              .where("name", SearchFilters.is("John"))
                                                              .returnAll()
                                                              .now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0), is("1"));
    assertThat(result.get(1), is("2"));
  }

  @Test
  public void searchReturnsObjectIdsAsLong() {

    context.checking(new Expectations() {{
      never(entityLoader);
    }});

    store(new User(1l, "John Adams"), new User(2l, "John Parker"));

    List<Long> result = searchEngine.searchIds(Long.class).inIndex(User.class)
                                                          .where("name", SearchFilters.is("John"))
                                                          .returnAll()
                                                          .now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0), is(1l));
    assertThat(result.get(1), is(2l));
  }

  @Test(expected = NotConfiguredIdConvertorException.class)
  public void notConfiguredIdConverter() {

    searchEngine = SearchEngineFactory.create(entityLoader, new IdConverterCatalog() {

      public IdConverter getConverter(Class aClass) {
        return null;
      }
    });

    context.checking(new Expectations() {{
      never(entityLoader);
    }});

    store(new User(1l, "John Adams"));

    List<Long> result = searchEngine.searchIds(Long.class).inIndex(User.class)
                                                          .where("name", SearchFilters.is("John"))
                                                          .returnAll()
                                                          .now();

    assertThat(result.size(), is(0));
  }

  @Test(expected = NotConfiguredIdConvertorException.class)
  public void missingIdConvertor() {

    searchEngine = SearchEngineFactory.create(entityLoader, new IdConverterCatalog() {
      @Override
      public IdConverter getConverter(Class aClass) {

        if (aClass.equals(String.class)) {
          return new StringIdConverter();
        }

        return null;
      }
    });

    context.checking(new Expectations() {{
      never(entityLoader);
    }});

    store(new User(1l, "John"));

    List<Long> result = searchEngine.searchIds(Long.class).inIndex(User.class)
                                                          .where("name", SearchFilters.is("John"))
                                                          .returnAll()
                                                          .now();

    assertThat(result.size(), is(0));
  }

  @Test(expected = NotConfiguredIndexingStrategyException.class)
  public void searchForObjectIdsWithoutGivenIndex() {

    context.checking(new Expectations() {{
      never(entityLoader);
    }});

    store(new User(1l, "John"));

    List<Long> result = searchEngine.searchIds(Long.class).where("name", SearchFilters.is("John")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void matchingBooleanFieldValues() {

    store(new Employee(1l, true));

    List<Employee> result = searchEngine.search(Employee.class).where("assigned", SearchFilters.is(true)).returnAll().now();

    assertThat(result.get(0).assigned, is(true));
  }

  @Test
  public void matchingPrivateFieldValue() {

    store(new User(1l, "John", "Adams"));

    List<User> result = searchEngine.search(User.class).where("family", SearchFilters.is("Adams")).returnAll().now();

    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchByOffset() {

    store(new User(1l, "Jack"));
    store(new User(2l, "Jack Briton"));
    store(new User(3l, "Jack Milar"));

    List<User> result = searchEngine.search(User.class).where("name", SearchFilters.is("Jack")).offset(1).returnAll().now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).name, is("Jack Briton"));
    assertThat(result.get(1).name, is("Jack Milar"));
  }

  @Test
  public void deleteOneIndexById(){

    store(new User(1l, "John"));

    searchEngine.delete("UserIndex", Lists.newArrayList(1l));

    List<Long> result = searchEngine.searchIds(Long.class).inIndex(User.class)
            .where("name", SearchFilters.is("John"))
            .returnAll()
            .now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void deleteMoreThanOneIndexByIds(){

    store(new User(1l, "John"));
    store(new User(2l, "John"));

    //name of index is configured in SearchId annotation over User class
    searchEngine.delete("UserIndex", Lists.newArrayList(1l, 2l));

    List<Long> result = searchEngine.searchIds(Long.class).inIndex(User.class)
            .where("name", SearchFilters.is("John"))
            .returnAll()
            .now();

    assertThat(result.size(), is(0));

  }

  @Test
  public void searchByWordSeparation() throws Exception {
    store(new User(1l, "John", "Adams", "separate by words"));

    List<User> result = searchEngine.search(User.class).where("description", SearchFilters.is("separate")).returnAll().now();

    assertThat(result.size(), is(1));
  }

  @Test
  public void shouldNotMatchWhenSearchByHalfWordWhereIsUsedWordSeparation() throws Exception {
    store(new User(1l, "John", "Adams", "separate by words"));

    List<User> result = searchEngine.search(User.class).where("description", SearchFilters.is("sepa")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void fullTextSearchForPropertyFromCollectionType() throws Exception {
    List<String> tags = new ArrayList<String>(){{
      add("example for");
      add("test");
    }};

    store(new User(1l, tags));

    List<User> result = searchEngine.search(User.class).where("tags", SearchFilters.is("ample")).returnAll().now();

    assertThat(result.size(), is(1));
  }

  @Test
  public void searchByFieldWithMultipleValuesOneOfWhichMatchesAnotherIndexedFieldValue() {

    store(new Employee(1l, "John", "Adams"));

    List<Employee> result = searchEngine.search(Employee.class).where("firstName", isAnyOf("Someone", "Adams"))
                                                               .returnAll()
                                                               .now();

    assertThat(result.size(), is(0));
  }

  @Test(expected = EmptySearchFilterException.class)
  public void searchFieldMatchingAnEmptyListOfValues() {

    List<Employee> result = searchEngine.search(Employee.class).where("firstName", SearchFilters.isAnyOf(Lists.<Long>newArrayList()))
                                                               .returnAll()
                                                               .now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByMatchingOneOfTwoFieldValues() {

    store(new Employee(1l, "John", "Brown"));
    store(new Employee(2l, "Tony", "John"));

    List<Employee> result = searchEngine.search(Employee.class)
            .where(Arrays.asList("firstName", "lastName"), SearchFilters.anyIs("John"))
            .returnAll().now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).firstName, is("John"));
    assertThat(result.get(1).lastName, is("John"));
  }


  @Test
  public void searchByMatchingOneOfTwoFieldValuesAndHaveEqualityForOneOfTheFields() {

    store(new Employee(1l, "John", "Brown"));
    store(new Employee(2l, "Tony", "John"));

    List<Employee> result = searchEngine.search(Employee.class)
            .where(Arrays.asList("firstName", "lastName"), SearchFilters.anyIs("John"))
            .where("firstName", SearchFilters.is("Tony"))
            .returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).firstName, is("Tony"));
    assertThat(result.get(0).lastName, is("John"));
  }

  @Test
  public void searchByFieldLessThanGivenDate() {

    store(aNewEmployee().id(1l).birthDate(aNewDate(2013, 12, 20)).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.lessThan(aNewDate(2013, 12, 25))).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(equalTo(1l)));
  }

  @Test
  public void searchByFieldGreaterThanGivenDate() {

    store(aNewEmployee().id(1l).birthDate(aNewDate(2013, 12, 10)).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.greaterThan(aNewDate(2013, 12, 1))).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchByFieldEqualToGivenDate() {

    store(aNewEmployee().id(1l).birthDate(aNewDate(2013, 12, 20)).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.equalTo(aNewDate(2013, 12, 20))).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchByFieldLessThanOrEqualToGivenDate() {

    store(aNewEmployee().id(1l).birthDate(aNewDate(2013, 12, 10)).build());
    store(aNewEmployee().id(2l).birthDate(aNewDate(2013, 12, 20)).build());
    store(aNewEmployee().id(3l).birthDate(aNewDate(2013, 12, 26)).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.lessThanOrEqualTo(aNewDate(2013, 12, 20))).returnAll().now();
    sortEmployeesById(result);

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(1l));
    assertThat(result.get(1).id, is(2l));
  }

  @Test
  public void searchByFieldGreaterThanOrEqualToGivenDate() {

    store(aNewEmployee().id(1l).birthDate(aNewDate(2013, 12, 10)).build());
    store(aNewEmployee().id(2l).birthDate(aNewDate(2013, 12, 20)).build());
    store(aNewEmployee().id(3l).birthDate(aNewDate(2013, 12, 26)).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.greaterThanOrEqualTo(aNewDate(2013, 12, 20))).returnAll().now();
    sortEmployeesById(result);

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(2l));
    assertThat(result.get(1).id, is(3l));
  }

  @Test
  public void sortByDateInAscendingOrder() {

    store(aNewEmployee().id(1l).birthDate(aNewDate(2013, 12, 20)).build());
    store(aNewEmployee().id(2l).birthDate(aNewDate(2013, 1, 1)).build());
    store(aNewEmployee().id(3l).birthDate(aNewDate(2013, 5, 18)).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.greaterThanOrEqualTo(aNewDate(2013, 1, 1)))
                                                               .sortBy("birthDate", SortOrder.ASCENDING, SortType.NUMERIC)
                                                               .returnAll().now();

    assertThat(result.size(), is(3));
    assertThat(result.get(0).id, is(equalTo(2l)));
    assertThat(result.get(1).id, is(equalTo(3l)));
    assertThat(result.get(2).id, is(equalTo(1l)));
  }

  @Test
  public void sortByDateInDescendingOrder() {

    store(aNewEmployee().id(1l).birthDate(aNewDate(2013, 1, 5)).build());
    store(aNewEmployee().id(2l).birthDate(aNewDate(2013, 5, 10)).build());
    store(aNewEmployee().id(3l).birthDate(aNewDate(2013, 8, 20)).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.greaterThanOrEqualTo(aNewDate(2013, 1, 1)))
                                                               .sortBy("birthDate", SortOrder.DESCENDING, SortType.NUMERIC)
                                                               .returnAll().now();

    assertThat(result.size(), is(3));
    assertThat(result.get(0).id, is(equalTo(3l)));
    assertThat(result.get(1).id, is(equalTo(2l)));
    assertThat(result.get(2).id, is(equalTo(1l)));
  }

  @Test
  public void sortByStringInAscendingOrder() {

    Date birthDate = aNewDate(2013, 1, 1);

    store(aNewEmployee().id(1l).firstName("John").birthDate(birthDate).build());
    store(aNewEmployee().id(2l).firstName("Adam").birthDate(birthDate).build());
    store(aNewEmployee().id(3l).firstName("Bob").birthDate(birthDate).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.equalTo(birthDate))
                                                               .sortBy("firstName", SortOrder.ASCENDING, SortType.TEXT)
                                                               .returnAll()
                                                               .now();

    assertThat(result.size(), is(3));
    assertThat(result.get(0).firstName, is("Adam"));
    assertThat(result.get(1).firstName, is("Bob"));
    assertThat(result.get(2).firstName, is("John"));
  }

  @Test
  public void sortByIntegerInDescendingOrder() {

    store(aNewEmployee().id(1l).firstName("John").age(18).build());
    store(aNewEmployee().id(2l).firstName("John").age(25).build());
    store(aNewEmployee().id(3l).firstName("John").age(20).build());

    List<Employee> result = searchEngine.search(Employee.class).where("firstName", SearchFilters.is("John"))
                                                               .sortBy("age", SortOrder.DESCENDING, SortType.TEXT)
                                                               .returnAll().now();

    assertThat(result.size(), is(3));
    assertThat(result.get(0).id, is(2l));
    assertThat(result.get(1).id, is(3l));
    assertThat(result.get(2).id, is(1l));
  }

  @Test
  public void searchByDateFieldsGreaterThanGivenTime() {

    store(aNewEmployee().id(1l).birthDate(aNewDate(2013, 12, 10, 9, 30)).build());
    store(aNewEmployee().id(2l).birthDate(aNewDate(2013, 12, 10, 11, 0)).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.greaterThan(aNewDate(2013, 12, 10, 9, 45)))
                                                               .returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(2l));
  }

  @Test
  public void nullDateFieldsAreNotIndexed() {

    store(aNewEmployee().id(1l).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.is("null")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByTextFieldWithoutValue() {

    store(aNewEmployee().id(1l).build());

    List<Employee> result = searchEngine.search(Employee.class).where("firstName", SearchFilters.is("John")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByNumberFieldWithoutValue() {

    store(aNewEmployee().id(1l).build());

    List<Employee> result = searchEngine.search(Employee.class).where("birthDate", SearchFilters.greaterThan(aNewDate(2013, 12, 10, 9, 45))).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchForMatchingAllOfTheFieldValues() {

    store(aNewEmployee().id(1l).tags(Arrays.asList("1", "2", "answered")).build());
    store(aNewEmployee().id(2l).tags(Arrays.asList("test")).build());

    List<Employee> result = searchEngine.search(Employee.class).where("tags", SearchFilters.is(Arrays.asList("1", "2", "answered"))).returnAll().now();

    assertThat(result.size(), is(1));
    assertThat(result.get(0).id, is(1l));
  }

  @Test
  public void searchForMatchingAnyOfTheFieldValues() {

    store(aNewEmployee().id(1l).age(18).build());
    store(aNewEmployee().id(2l).age(19).build());
    store(aNewEmployee().id(3l).age(20).build());

    List<Employee> result = searchEngine.search(Employee.class).where("age", SearchFilters.isAnyOf(Arrays.asList(18, 20)))
                                                               .sortBy("age", SortOrder.ASCENDING, SortType.NUMERIC)
                                                               .returnAll().now();

    assertThat(result.size(), is(2));
    assertThat(result.get(0).id, is(equalTo(1l)));
    assertThat(result.get(1).id, is(equalTo(3l)));
  }

  @Test
  public void defaultSearchWithAnnotatedProperty() throws Exception {
    Ticket ticket = new Ticket(1l, "Some Title", "Description");
    ticket.setComment("some comment is add");
    store(ticket);


    List<Ticket> tickets = searchEngine.search(Ticket.class).where("comment", SearchFilters.is("some")).returnAll().now();

    assertThat(tickets.size(), is(equalTo(1)));
  }

  @Test
  public void fullWordSearchWithAnnotatedProperty() throws Exception {
    store(new Ticket(1l, "Some Title", "Description"));

    List<Ticket> tickets = searchEngine.search(Ticket.class).where("title", SearchFilters.is("Title")).returnAll().now();

    assertThat(tickets.size(), is(equalTo(1)));
    assertThat(tickets.get(0).getTitle(), is(equalTo("Some Title")));
  }

  @Test
  public void fullTextSearchWithAnnotatedProperty() throws Exception {
    store(new Ticket(1l, "Some Title", "Description"));

    List<Ticket> tickets = searchEngine.search(Ticket.class).where("description", SearchFilters.is("desc")).returnAll().now();

    assertThat(tickets.size(), is(equalTo(1)));
    assertThat(tickets.get(0).getDescription(), is(equalTo("Description")));
  }

  @Test
  public void searchByDateWithAnnotatedProperty() throws Exception {

    Ticket ticket = new Ticket(1l, "Title", "Description");
    ticket.setCreationDate(aNewDate(2013, 12, 20));

    store(ticket);

    List<Ticket> result = searchEngine.search(Ticket.class).where("creationDate", SearchFilters.lessThanOrEqualTo(aNewDate(2013, 12, 25))).returnAll().now();

    assertThat(result.size(), is(1));
  }

  @Test
  public void shouldNotFindResultsWhenSearchByIgnoredProperty() throws Exception {
    Ticket ticket = new Ticket(1l, "Title", "Description");
    ticket.setDetails("some info");

    store(ticket);

    List<Ticket> result = searchEngine.search(Ticket.class).where("details", SearchFilters.is("info")).returnAll().now();

    assertThat(result.size(), is(0));
  }

  @Test
  public void searchByEmbeddedIndex() throws Exception {

    AddressIndex addressIndex = new AddressIndex(100l, "CityName", "5000");
    CustomerIndex customerIndex = new CustomerIndex(200l, addressIndex);

    repository.store(customerIndex.getEntityId(), customerIndex);
    searchEngine.register(customerIndex);

    List<CustomerIndex> result = searchEngine.search(CustomerIndex.class).where("address", SearchFilters.is("5000")).returnAll().now();

    assertThat(result.size(), is(1));
  }

  @Test
  public void fullTextSearchByEmbeddedIndex() throws Exception {

    AddressIndex addressIndex = new AddressIndex(100l, "CityName", "5000");
    CustomerIndex customerIndex = new CustomerIndex(200l, addressIndex);

    repository.store(customerIndex.getEntityId(), customerIndex);
    searchEngine.register(customerIndex);

    List<CustomerIndex> result = searchEngine.search(CustomerIndex.class).where("address", SearchFilters.is("cit")).returnAll().now();

    assertThat(result.size(), is(1));
  }

  @Test
  public void searchInEmbeddedIndexFromSameType() throws Exception {

    AddressIndex oldAddressIndex = new AddressIndex(100l, "Some City", "5000");
    AddressIndex newAddressIndex = new AddressIndex(200l, "Another City", "2000");
    EmployeeIndex employeeIndex = new EmployeeIndex(300l, "employee name", oldAddressIndex, newAddressIndex);

    repository.store(300l, employeeIndex);
    searchEngine.register(employeeIndex);

    List<EmployeeIndex> oldAddressSearchResult = searchEngine.search(EmployeeIndex.class).where("oldAddress", SearchFilters.is("Some")).returnAll().now();
    assertThat(oldAddressSearchResult.size(), is(1));

    List<EmployeeIndex> newAddressSearchResult = searchEngine.search(EmployeeIndex.class).where("newAddress", SearchFilters.is("Another")).returnAll().now();
    assertThat(newAddressSearchResult.size(), is(1));
  }

  @Test
  public void searchInComplexEmbeddedIndex() throws Exception {

    AddressIndex addressIndex = new AddressIndex(100l, "CityName", "5000");
    CustomerIndex customerIndex = new CustomerIndex(200l, addressIndex);
    CompanyIndex companyIndex = new CompanyIndex(300l, "company", customerIndex);

    repository.store(300l, companyIndex);
    searchEngine.register(companyIndex);

    List<CompanyIndex> result = searchEngine.search(CompanyIndex.class).where("customer", SearchFilters.is("city")).returnAll().now();
    assertThat(result.size(), is(1));
  }

  @Test
  public void skipMissingEmbeddedIndex() throws Exception {

    CustomerIndex customerIndex = new CustomerIndex(200l, "customer name");
    CompanyIndex companyIndex = new CompanyIndex(300l, "company", customerIndex);

    repository.store(300l, companyIndex);
    searchEngine.register(companyIndex);

    List<CompanyIndex> result = searchEngine.search(CompanyIndex.class).where("customer", SearchFilters.is("customer name")).returnAll().now();
    assertThat(result.size(), is(1));
  }

  @Test
  public void escapeSpecialSymbols() throws Exception {
    store(new User(1l, ":,+-=<>"));

    List<User> result = searchEngine.search(User.class).where(":,+-=<>").returnAll().now();

    assertThat(result.size(), is(1));
  }

  @Test
  public void wrapWordsWithSpecialSymbols() throws Exception {
    store(new User(10l, "user d8:66:66"));
    store(new User(20l, "user d8:66:b6"));
    store(new User(30l, "user d8:66:b7"));

    List<User> result = searchEngine.search(User.class).where("d8:66:66").returnAll().now();

    assertThat(result.size(), is(1));
  }

  private void store(User... users) {

    for (User user : users) {
      repository.store(user.id, user);
      searchEngine.register(user);
    }
  }

  private void store(Employee... employees) {

    for (Employee employee : employees) {
      repository.store(employee.id, employee);
      searchEngine.register(employee);
    }
  }

  private void store(Ticket... tickets) {

    for (Ticket ticket : tickets) {
      repository.store(ticket.getId(), ticket);
      searchEngine.register(ticket);
    }
  }

  private void sortEmployeesById(List<Employee> employees) {

    Collections.sort(employees, new Comparator<Employee>() {
      @Override
      public int compare(Employee o1, Employee o2) {

        if (o1.id < o2.id) {
          return -1;
        } else {
          return 1;
        }
      }
    });
  }

  private Date aNewDate(int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, day);
    return calendar.getTime();
  }

  private Date aNewDate(int year, int month, int day, int hour, int minutes) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, day, hour, minutes);
    return calendar.getTime();
  }
}
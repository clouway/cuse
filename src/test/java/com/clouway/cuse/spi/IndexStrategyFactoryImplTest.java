package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.FullTextSearch;
import com.clouway.cuse.spi.annotations.FullWordSearch;
import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;
import com.google.inject.util.Providers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

//import com.google.inject.Provides;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class IndexStrategyFactoryImplTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();

  @Mock
  IdConverterCatalog idConverterCatalog;

  private IndexStrategyFactory factory;

  @Before
  public void setUp() throws Exception {
    SearchAnnotationsCatalog searchAnnotationsCatalog = new InMemorySearchAnnotationsCatalog();
    factory = new IndexStrategyFactoryImpl(Providers.of(idConverterCatalog), Providers.of(searchAnnotationsCatalog));
  }

  @Test
  public void getIndexName() throws Exception {

    IndexingStrategy indexingStrategy = factory.create(TestEntity.class);

    assertThat("index name is not correct", indexingStrategy.getIndexName(), is("IndexName"));
  }

  @Test
  public void getIdOfGivenInstance() throws Exception {

    context.checking(new Expectations() {{
      oneOf(idConverterCatalog).getConverter(Long.class);
      will(returnValue(new IdConverter<Long>(){
        @Override
        public List<Long> convert(List<String> values) {
          return null;
        }

        @Override
        public String convertFrom(Object id) {
          return Long.toString((Long) id);
        }
      }));
    }});

    IndexingStrategy indexingStrategy = factory.create(TestEntity.class);

    String entityId = indexingStrategy.getId(new TestEntity(100l));

    assertThat("entity id is not correct", entityId, is("100"));
  }

  @Test
  public void getFullWordSearchProperties() throws Exception {

    IndexingStrategy indexingStrategy = factory.create(TestEntity.class);

    List<String> wordSearchProperties = indexingStrategy.getIndexingSchema().getFields();

    assertThat("word search properties are not correct", wordSearchProperties, is(Arrays.asList("wordProperty", "anotherWordProperty")));
  }

  @Test
  public void getFullTextSearchProperties() throws Exception {

    IndexingStrategy indexingStrategy = factory.create(TestEntity.class);

    List<String> fullTextSearchProperties = indexingStrategy.getIndexingSchema().getFullText();

    assertThat("full test search properties are not correct", fullTextSearchProperties, is(Arrays.asList("fullTextProperty", "anotherFullTextProperty")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void trowExceptionWhenMissingSearchIdAnnotatedProperty() throws Exception {

    IndexingStrategy indexingStrategy = factory.create(TestEntity.class);
    indexingStrategy.getId(new TestIdEntity(20l));
  }

  @SearchIndex(name = "IndexName")
  class TestEntity {

    @SearchId
    private Long entityId;

    @FullWordSearch
    private String wordProperty = "some";

    @FullWordSearch
    private String anotherWordProperty = "some some";

    @FullTextSearch
    private String fullTextProperty = "full text";

    @FullTextSearch
    private String anotherFullTextProperty = "full text search";

    TestEntity(Long entityId) {
      this.entityId = entityId;
    }
  }

  @SearchIndex(name = "TestIdEntityIndex")
  class TestIdEntity {

    private Long entityId;

    TestIdEntity(Long entityId) {
      this.entityId = entityId;
    }
  }
}

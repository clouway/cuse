package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.FullTextSearch;
import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;
import com.google.inject.util.Providers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
    IndexSchemaFillActionsCatalog indexSchemaFillActionsCatalog = new IndexSchemaFillActionsCatalog() {
      @Override
      public IndexSchemaFillAction getFillAction(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
          if (FullTextSearch.class.getSimpleName().equals(annotation.annotationType().getSimpleName())) {
            return new IndexSchemaFillAction() {
              @Override
              public void fill(IndexingSchema.IndexingSchemaBuilder indexingSchemaBuilder, String propertyName) {
                indexingSchemaBuilder.fullTextFields(propertyName);
              }
            };
          }
        }
        return null;
      }
    };

    factory = new IndexStrategyFactoryImpl(Providers.of(idConverterCatalog), Providers.of(indexSchemaFillActionsCatalog));
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
      will(returnValue(new IdConverter<Long>() {
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

  @Test(expected = IllegalArgumentException.class)
  public void trowExceptionWhenMissingSearchIdAnnotatedProperty() throws Exception {

    IndexingStrategy indexingStrategy = factory.create(TestEntity.class);
    indexingStrategy.getId(new TestIdEntity(20l));
  }

  @Test
  public void addAnnotatedPropertyToIndexSchema() throws Exception {
    IndexingStrategy indexingStrategy = factory.create(TestEntity.class);

    List<String> defaultSearchProperties = indexingStrategy.getIndexingSchema().getFields();
    List<String> searchProperties = indexingStrategy.getIndexingSchema().getFullText();

    assertThat("incorrect default schema properties", defaultSearchProperties, is(Arrays.asList("entityId", "defaultProperty")));
    assertThat("incorrect index schema properties", searchProperties, is(Arrays.asList("searchProperty")));
  }
  @SearchIndex(name = "IndexName")
  static class TestEntity {

    @SearchId
    private Long entityId;

    @FullTextSearch
    private String searchProperty = "full text";

    private String defaultProperty = "default";

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

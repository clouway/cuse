package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.FullTextSearch;
import com.clouway.cuse.spi.annotations.FullWordSearch;
import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;
import com.google.inject.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.clouway.cuse.spi.IndexingSchema.aNewIndexingSchema;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class IndexStrategyFactoryImpl implements IndexStrategyFactory {

  private IdConverterCatalog idConverterCatalog;

  @Inject
  public IndexStrategyFactoryImpl(IdConverterCatalog idConverterCatalog) {
    this.idConverterCatalog = idConverterCatalog;
  }

  @Override
  public IndexingStrategy create(final Class<?> indexClazz) {
    SearchIndex annotation = indexClazz.getAnnotation(SearchIndex.class);

    if(annotation != null) {
      final String indexName = annotation.name();

      return new IndexingStrategy() {

        @Override
        public String getIndexName() {
          return indexName;
        }

        @Override
        public String getId(Object instance) {
          return getInstanceId(instance);
        }

        @Override
        public IndexingSchema getIndexingSchema() {
          return getIndexSchema(indexClazz);
        }
      };
    } else {
      return null;
    }
  }

  private String getInstanceId(Object instance) {
    Field[] fields = instance.getClass().getDeclaredFields();
    for (Field field : fields) {
      SearchId idAnnotation = field.getAnnotation(SearchId.class);
      if(idAnnotation != null) {
        try {
          field.setAccessible(true);
          Object id = field.get(instance);
          IdConverter converter = idConverterCatalog.getConverter(field.getType());
          return converter.convertFrom(id);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  private IndexingSchema getIndexSchema(Class<?> indexClazz) {
    List<String> fullWordSearchList = new ArrayList<String>();
    List<String> fullTextSearchList = new ArrayList<String>();

    Field[] fields = indexClazz.getDeclaredFields();
    for (Field field : fields) {

      fillPropertiesWithAnnotation(fullWordSearchList, field, FullWordSearch.class);
      fillPropertiesWithAnnotation(fullTextSearchList, field, FullTextSearch.class);
    }

    return aNewIndexingSchema()
            .fields(convertToArray(fullWordSearchList))
            .fullTextFields(convertToArray(fullTextSearchList))
            .build();
  }

  private void fillPropertiesWithAnnotation(List<String> propertyList, Field field, Class<? extends Annotation> annotationClass) {
    Annotation annotatedProperty = field.getAnnotation(annotationClass);
    if(annotatedProperty != null) {
      propertyList.add(field.getName());
    }
  }

  private String[] convertToArray(List<String> propertyList) {
    return propertyList.toArray(new String[propertyList.size()]);
  }
}

package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.clouway.cuse.spi.IndexingSchema.aNewIndexingSchema;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
class IndexStrategyFactoryImpl implements IndexStrategyFactory {

  private final Provider<IdConverterCatalog> idConverterCatalog;
  private final Provider<SearchAnnotationsCatalog> searchAnnotationsCatalog;

  @Inject
  public IndexStrategyFactoryImpl(
          Provider<IdConverterCatalog> idConverterCatalog,
          Provider<SearchAnnotationsCatalog> searchAnnotationsCatalog) {
    this.idConverterCatalog = idConverterCatalog;
    this.searchAnnotationsCatalog = searchAnnotationsCatalog;
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
          IdConverter converter = idConverterCatalog.get().getConverter(field.getType());
          return converter.convertFrom(id);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }

    throw new IllegalArgumentException("Missing search id annotation for class : " + instance.getClass().getName());
  }

  private IndexingSchema getIndexSchema(Class<?> indexClazz) {
    List<String> fullWordSearchList = new ArrayList<String>();
    List<String> fullTextSearchList = new ArrayList<String>();

    Field[] fields = indexClazz.getDeclaredFields();
    for (Field field : fields) {

      Set<SearchAnnotation> searchAnnotations = searchAnnotationsCatalog.get().getSearchAnnotations();

      for (SearchAnnotation searchAnnotation : searchAnnotations) {
        if(searchAnnotation.isFullTextSearch()) {
          fillPropertiesWithAnnotation(fullTextSearchList, field, searchAnnotation.getAnnotationClass());
        }else {
          fillPropertiesWithAnnotation(fullWordSearchList, field, searchAnnotation.getAnnotationClass());
        }
      }
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

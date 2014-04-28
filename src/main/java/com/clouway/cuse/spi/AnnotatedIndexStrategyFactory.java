package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.lang.reflect.Field;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
class AnnotatedIndexStrategyFactory implements IndexStrategyFactory {

  private final Provider<IdConverterCatalog> idConverterCatalog;

  @Inject
  public AnnotatedIndexStrategyFactory(Provider<IdConverterCatalog> idConverterCatalog) {
    this.idConverterCatalog = idConverterCatalog;
  }

  @Override
  public IndexingStrategy create(final Class<?> indexClazz) {
    SearchIndex searchIndex = indexClazz.getAnnotation(SearchIndex.class);

    if (searchIndex != null) {
      final String indexName = searchIndex.name();

      return new IndexingStrategy() {

        @Override
        public String getIndexName() {
          return indexName;
        }

        @Override
        public String getId(Object instance) {
          return getInstanceId(instance);
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
      if (idAnnotation != null) {
        try {
          field.setAccessible(true);
          Object id = field.get(instance);
          IdConverter converter = idConverterCatalog.get().getConverter(field.getType());
          if (converter == null) {
            throw new NotConfiguredIdConvertorException();
          }
          return converter.convertFrom(id);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }

    throw new IllegalArgumentException("Missing search id annotation for class : " + instance.getClass().getName());
  }
}

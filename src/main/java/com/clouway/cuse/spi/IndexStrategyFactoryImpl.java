package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.SearchId;
import com.clouway.cuse.spi.annotations.SearchIndex;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.lang.reflect.Field;

import static com.clouway.cuse.spi.IndexingSchema.aNewIndexingSchema;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
class IndexStrategyFactoryImpl implements IndexStrategyFactory {

  private final Provider<IdConverterCatalog> idConverterCatalog;
  private final Provider<IndexSchemaFillActionsCatalog> indexSchemaFillerActionsCatalog;

  @Inject
  public IndexStrategyFactoryImpl(
          Provider<IdConverterCatalog> idConverterCatalog,
          Provider<IndexSchemaFillActionsCatalog> indexSchemaFillerActionsCatalog) {
    this.idConverterCatalog = idConverterCatalog;
    this.indexSchemaFillerActionsCatalog = indexSchemaFillerActionsCatalog;
  }

  @Override
  public IndexingStrategy create(final Class<?> indexClazz) {
    SearchIndex searchIndex = indexClazz.getAnnotation(SearchIndex.class);

    if(searchIndex != null) {
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
    IndexingSchema.IndexingSchemaBuilder indexingSchemaBuilder = aNewIndexingSchema();

    Field[] fields = indexClazz.getDeclaredFields();
    for (Field field : fields) {

      IndexSchemaFillAction action = indexSchemaFillerActionsCatalog.get().getFillAction(field.getAnnotations());
      if(action != null) {
        action.fill(indexingSchemaBuilder, field.getName());
      } else {
        indexingSchemaBuilder.fields(field.getName());
      }
    }

    return indexingSchemaBuilder.build();
  }
}

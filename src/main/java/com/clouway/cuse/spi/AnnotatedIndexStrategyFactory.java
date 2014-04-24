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
class AnnotatedIndexStrategyFactory implements IndexStrategyFactory {

  private final Provider<IdConverterCatalog> idConverterCatalog;
  private final Provider<IndexSchemaFillActionsCatalog> indexSchemaFillerActionsCatalog;

  @Inject
  public AnnotatedIndexStrategyFactory(
          Provider<IdConverterCatalog> idConverterCatalog,
          Provider<IndexSchemaFillActionsCatalog> indexSchemaFillerActionsCatalog) {
    this.idConverterCatalog = idConverterCatalog;
    this.indexSchemaFillerActionsCatalog = indexSchemaFillerActionsCatalog;
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

  private IndexingSchema getIndexSchema(Class<?> indexClazz) {
    IndexingSchema.IndexingSchemaBuilder indexingSchemaBuilder = aNewIndexingSchema();

    getIndexSchema(indexClazz, "", indexingSchemaBuilder);

    return indexingSchemaBuilder.build();
  }

  private IndexingSchema getIndexSchema(Class<?> indexClazz, String parent, IndexingSchema.IndexingSchemaBuilder indexingSchemaBuilder) {

    Field[] fields = indexClazz.getDeclaredFields();
    for (Field field : fields) {

      String name = field.getName();

      if (!"".equals(parent)) {
        name = parent.concat("_").concat(field.getName());
      }

      IndexSchemaFillAction action = indexSchemaFillerActionsCatalog.get().getFillAction(field.getAnnotations());

      if (action != null) {
        action.fill(indexingSchemaBuilder, name);
      } else if (isIndex(field)) {
        getIndexSchema(field.getType(), field.getName(), indexingSchemaBuilder);
      } else {
        indexingSchemaBuilder.fields(name);
      }
    }

    return indexingSchemaBuilder.build();
  }

  private boolean isIndex(Field field) {
    SearchIndex annotation = field.getType().getAnnotation(SearchIndex.class);
    return annotation != null;
  }
}

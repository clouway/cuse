package com.clouway.cuse.spi;

import com.clouway.cuse.spi.annotations.FullTextSearch;
import com.clouway.cuse.spi.annotations.FullWordSearch;
import com.clouway.cuse.spi.annotations.Ignore;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Georgi Georgiev (GeorgievJon@gmail.com)
 */
public class InMemoryIndexSchemaFillActionsCatalog implements IndexSchemaFillActionsCatalog {

  private Map<Class<? extends Annotation>, IndexSchemaFillAction> fillerActionMap = new HashMap<Class<? extends Annotation>, IndexSchemaFillAction>(){{
    put(FullWordSearch.class, new IndexSchemaFillAction() {
      @Override
      public void fill(IndexingSchema.IndexingSchemaBuilder indexingSchemaBuilder, String propertyName) {
        indexingSchemaBuilder.fullWordFields(propertyName);
      }
    });

    put(FullTextSearch.class, new IndexSchemaFillAction() {
      @Override
      public void fill(IndexingSchema.IndexingSchemaBuilder indexingSchemaBuilder, String propertyName) {
        indexingSchemaBuilder.fullTextFields(propertyName);
      }
    });

    put(Ignore.class, new IndexSchemaFillAction() {
      @Override
      public void fill(IndexingSchema.IndexingSchemaBuilder indexingSchemaBuilder, String propertyName) {
        //should not add property to index schema
      }
    });
  }};

  @Override
  public IndexSchemaFillAction getFillAction(Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if(fillerActionMap.containsKey(annotation.annotationType())) {
       return fillerActionMap.get(annotation.annotationType());
      }
    }
    return null;
  }
}

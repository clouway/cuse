package com.clouway.cuse.gae;

import com.clouway.cuse.gae.filedindexing.FieldCriteria;
import com.clouway.cuse.gae.filedindexing.FieldIndexer;
import com.clouway.cuse.spi.IndexRegister;
import com.clouway.cuse.spi.IndexingStrategy;
import com.clouway.cuse.spi.annotations.SearchIndex;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeSearchApiIndexRegister implements IndexRegister {

  private Map<FieldCriteria, FieldIndexer> actionCriterias = new HashMap<FieldCriteria, FieldIndexer>();

  public GaeSearchApiIndexRegister(Map<FieldCriteria, FieldIndexer> actionCriterias) {
    this.actionCriterias = actionCriterias;
  }

  @Override
  public void register(Object instance, IndexingStrategy strategy) {
    String documentId = strategy.getId(instance);

    Document document = buildDocument(instance, documentId);

    addDocumentInIndex(strategy.getIndexName(), document);
  }

  public void delete(String indexName, List<Long> objectIds) {
    List<String> stringObjectIds = new ArrayList<String>();
    for (Long id : objectIds) {
      stringObjectIds.add(String.valueOf(id));
    }
    loadIndex(indexName).delete(stringObjectIds);
  }

  private Index loadIndex(String indexName) {
    return SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder()
            .setName(indexName));
  }

  private void addDocumentInIndex(String indexName, Document document) {
    loadIndex(indexName).put(document);
  }

  private Document buildDocument(Object instance, String documentId) {

    Document.Builder documentBuilder = Document.newBuilder().setId(documentId);

    buildDocument("", instance, documentBuilder);

    return documentBuilder.build();
  }

  private Document.Builder buildDocument(String defaultName, Object instance, Document.Builder documentBuilder) {
    for (java.lang.reflect.Field field : instance.getClass().getDeclaredFields()) {

      Object fieldInstanceValue = getFieldValue(instance, field);

      String fieldName = field.getName();

      if (!"".equals(defaultName)) {
        fieldName = defaultName;
      }

//      if (!"".equals(parent)) {
//        fieldName = parent.concat("_").concat(field.getName());
//      }

      SearchIndex annotation = field.getType().getAnnotation(SearchIndex.class);
      if (annotation == null) {
        //sub index not found index the filed
        for (FieldCriteria criteria : actionCriterias.keySet()) {

          if (criteria.match(field)) {
            FieldIndexer action = actionCriterias.get(criteria);
            List<Field> fieldList = action.index(fieldName, field, fieldInstanceValue);

            for (Field newFiled : fieldList) {
              documentBuilder.addField(newFiled);
            }
          }
        }
      } else {
        //sub index found
        Object childInstance = getFieldValue(instance, field);
        if(childInstance != null) {
          buildDocument(fieldName, childInstance, documentBuilder);
        }
      }
    }

    return documentBuilder;
  }


  private Object getFieldValue(Object instance, java.lang.reflect.Field field) {

    try {

      java.lang.reflect.Field declaredField = instance.getClass().getDeclaredField(field.getName());
      declaredField.setAccessible(true);

      return declaredField.get(instance);

    } catch (IllegalAccessException e) {
      throw new FieldNotAccessibleException();
    } catch (NoSuchFieldException e) {
      throw new NoFieldOfASpecifiedNameException();
    }
  }
}

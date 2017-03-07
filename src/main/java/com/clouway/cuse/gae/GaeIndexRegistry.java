package com.clouway.cuse.gae;

import com.clouway.cuse.gae.filedindexing.FieldCriteria;
import com.clouway.cuse.gae.filedindexing.FieldIndexer;
import com.clouway.cuse.spi.IndexCreationFailureException;
import com.clouway.cuse.spi.IndexRegistry;
import com.clouway.cuse.spi.IndexingStrategy;
import com.clouway.cuse.spi.annotations.SearchIndex;
import com.google.appengine.api.search.*;
import com.google.common.collect.Lists;
import com.google.inject.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeIndexRegistry implements IndexRegistry {
  private static final String INDEX_CREATION_FORMAT = "Index wasn't created due: %s (%s)";
  private static final Integer MAX_BATCH_SIZE = 200;

  private final Map<FieldCriteria, FieldIndexer> actionCriterias;
  private final Provider<SearchService> searchService;

  public GaeIndexRegistry(Map<FieldCriteria, FieldIndexer> actionCriterias, Provider<SearchService> searchService) {
    this.actionCriterias = actionCriterias;
    this.searchService = searchService;
  }

  @Override
  public void register(Object instance, IndexingStrategy strategy) {
    String documentId = strategy.getId(instance);

    Document document = buildDocument(instance, documentId);

    indexDocument(strategy.getIndexName(), document);
  }

  @Override
  public void registerAll(List<? extends Object> instances, IndexingStrategy strategy) {
    //There is a Index.put() limitation to 200 documents as described :https://cloud.google.com/appengine/docs/standard/python/search/best_practices
    List<? extends List<?>> parts = Lists.partition(instances, MAX_BATCH_SIZE);
    for (List<?> part : parts) {
      List<Document> documents = Lists.newArrayList();
      for (Object instance : part) {
        String documentId = strategy.getId(instance);
        Document document = buildDocument(instance, documentId);
        documents.add(document);
      }
      indexDocument(strategy.getIndexName(), documents.toArray(new Document[documents.size()]));
    }
  }

  @Override
  public void delete(String indexName, List<Long> objectIds) {
    List<String> stringObjectIds = new ArrayList<String>();
    for (Long id : objectIds) {
      stringObjectIds.add(String.valueOf(id));
    }
    getIndex(indexName).delete(stringObjectIds);
  }

  private void indexDocument(String indexName, Document... document) {
    final int maxRetry = 3;
    int attempts = 0;
    int delay = 2;
    PutResponse putResponse;
    while (true) {
      try {
        putResponse = getIndex(indexName).put(document);
      } catch (PutException e) {
        if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())
                && ++attempts < maxRetry) { // retrying
          try {
            Thread.sleep(delay * 1000);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
          delay *= 2; // exponential backoff
          continue;
        } else {
          throw e; // otherwise throw
        }
      }
      break;
    }
    for (OperationResult result : putResponse.getResults()) {
      if (result.getCode() != StatusCode.OK) {
        throw new IndexCreationFailureException(String.format(INDEX_CREATION_FORMAT, result.getCode(), result.getMessage()));
      }
    }
  }

  private Index getIndex(String indexName) {
    return searchService.get().getIndex(IndexSpec.newBuilder().setName(indexName));
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
        if (childInstance != null) {
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

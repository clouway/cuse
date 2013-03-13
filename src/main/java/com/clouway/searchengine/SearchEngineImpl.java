package com.clouway.searchengine;

import com.google.appengine.api.search.Consistency;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class SearchEngineImpl implements SearchEngine {

  private final EntityLoader entityLoader;
  private final IndexingStrategyCatalog indexingStrategyCatalog;

  public SearchEngineImpl(EntityLoader entityLoader, IndexingStrategyCatalog indexingStrategyCatalog) {
    this.entityLoader = entityLoader;
    this.indexingStrategyCatalog = indexingStrategyCatalog;
  }

  public void register(Object instance) {

    Class instanceClass = instance.getClass();
    IndexingStrategy strategy = indexingStrategyCatalog.get(instanceClass);

    if (strategy == null) {
      throw new InvalidIndexingStrategyException();
    }

    String documentId = strategy.getId(instance);
    String indexName = strategy.getIndexName();
    List<String> indexingFields = strategy.getFields();

    Document.Builder documentBuilder = Document.newBuilder().setId(documentId);

    Map<String, String> documentFields = new HashMap<String, String>();
    for (java.lang.reflect.Field field : instanceClass.getDeclaredFields()) {

      if (indexingFields.contains(field.getName())) {

        String fieldValue = "";
        try {
          fieldValue = String.valueOf(instanceClass.getField(field.getName()).get(instance));
        } catch (NoSuchFieldException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }

        documentFields.put(field.getName(), fieldValue);
      }
    }

    for (String field : documentFields.keySet()) {
      String fieldValue = documentFields.get(field);
      documentBuilder.addField(Field.newBuilder().setName(field).setText(fieldValue));
    }

    loadIndex(indexName).add(documentBuilder.build());
  }

  public <T> Search.SearchBuilder<T> search(Class<T> clazz) {
    return new Search.SearchBuilder<T>(clazz, entityLoader, indexingStrategyCatalog);
  }

  private Index loadIndex(String indexName) {
    return SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder()
                                                  .setName(indexName)
                                                  .setConsistency(Consistency.PER_DOCUMENT));
  }
}

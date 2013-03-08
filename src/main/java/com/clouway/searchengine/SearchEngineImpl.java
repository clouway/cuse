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
    String docId = strategy.getId(instance);
    String indexName = strategy.getIndexName();
    List<String> indexingFields = strategy.getFields();

    Document.Builder builder = Document.newBuilder();
    builder.setId(String.valueOf(docId));

    Map<String, String> textFieldMap = new HashMap<String, String>();
    for (java.lang.reflect.Field field : instanceClass.getDeclaredFields()) {

      if (indexingFields.contains(field.getName())) {

        String value = "";
        try {
          value = String.valueOf(instanceClass.getField(field.getName()).get(instance));
        } catch (NoSuchFieldException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }

        textFieldMap.put(field.getName(), value);
      }
    }

    for (String fieldName : textFieldMap.keySet()) {
      String value = textFieldMap.get(fieldName);
      builder.addField(Field.newBuilder().setName(fieldName).setText(value));
    }

    loadIndex(indexName).add(builder.build());
  }

  public Search.SearchBuilder search(Class clazz) {
    return new Search.SearchBuilder(clazz, entityLoader, indexingStrategyCatalog);
  }

  private Index loadIndex(String indexName) {
    return SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder()
            .setName(indexName)
            .setConsistency(Consistency.PER_DOCUMENT));
  }
}

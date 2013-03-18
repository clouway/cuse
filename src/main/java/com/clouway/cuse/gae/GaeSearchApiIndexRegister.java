package com.clouway.cuse.gae;

import com.clouway.cuse.spi.IndexRegister;
import com.clouway.cuse.spi.IndexWriter;
import com.clouway.cuse.spi.IndexingSchema;
import com.clouway.cuse.spi.IndexingStrategy;
import com.google.appengine.api.search.Consistency;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeSearchApiIndexRegister implements IndexRegister {
  @Override
  public void register(Object instance, IndexingStrategy strategy) {
    String documentId = strategy.getId(instance);

    IndexingSchema indexingSchema = strategy.getIndexingSchema();
    List<String> fields = indexingSchema.getFields();
    List<String> fullTextFields = indexingSchema.getFullText();

    Document document = buildDocument(instance, documentId, fields, fullTextFields);

    addDocumentInIndex(strategy.getIndexName(), document);
  }



  private void addDocumentInIndex(String indexName, Document document) {
    loadIndex(indexName).add(document);
  }

  private Index loadIndex(String indexName) {
    return SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder()
            .setName(indexName)
            .setConsistency(Consistency.PER_DOCUMENT));
  }

  private Document buildDocument(Object instance, String documentId, List<String> fields, List<String> fullTextFields) {

    Document.Builder documentBuilder = Document.newBuilder().setId(documentId);

    Map<String, String> documentFields = new HashMap<String, String>();
    for (java.lang.reflect.Field field : instance.getClass().getDeclaredFields()) {

      if (fields.contains(field.getName())) {
        String fieldValue = getFieldValue(instance, field);
        documentFields.put(field.getName(), fieldValue);
      }

      if (fullTextFields.contains(field.getName())) {
        Set<String> fieldValues = new IndexWriter().createIndex(getFieldValue(instance, field));
        for (String fieldValue : fieldValues) {
          documentBuilder.addField(Field.newBuilder().setName(field.getName()).setText(fieldValue));
        }
      }
    }

    for (String field : documentFields.keySet()) {
      String fieldValue = documentFields.get(field);
      documentBuilder.addField(Field.newBuilder().setName(field).setText(fieldValue));
    }

    return documentBuilder.build();
  }

  private String getFieldValue(Object instance, java.lang.reflect.Field field) {

    String fieldValue = "";

    try {

      java.lang.reflect.Field declaredField = instance.getClass().getDeclaredField(field.getName());
      declaredField.setAccessible(true);
      fieldValue = String.valueOf(declaredField.get(instance));

    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }

    return fieldValue;
  }
}
